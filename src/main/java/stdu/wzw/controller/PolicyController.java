package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import com.hankcs.hanlp.HanLP;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.condition.PolicyCondition;
import stdu.wzw.model.*;
import stdu.wzw.repository.EsPolicyRepository;
import stdu.wzw.service.*;
import stdu.wzw.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 政策管理
 * 文档处理有多种情况，件中有空的，文件已存在，上传失败和成功，先不处理，让能成功的上传成功。
 */
@RestController
@RequestMapping(value = {"/policy"})
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private Policy_contentService policy_contentService;

    @Autowired
    private Policy_typeService policy_typeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private Policy_analysisService policy_analysisService;
    @Autowired
    private UserService userService;

    @Autowired
    private EsPolicyRepository esPolicyRepository;

    @Autowired
    private ExecuteAndAbolishService executeAndAbolishService;

    /**
     * 根据政策id获取施行日期信息
     *
     * @param policyId
     * @return
     */
    @RequestMapping("getExecuteAndAbolish")
    public List<ExecuteAndAbolish> getExecuteAndAbolish(Integer policyId) {
        return executeAndAbolishService.findByPolicyId(policyId);
    }

    /**
     * @param policyId
     * @param analysisName
     * @return
     */
    @RequestMapping(value = "/showAnalysis")
    public ModelAndView showAnalysis(Integer policyId, String analysisName) {
        ModelAndView mv = new ModelAndView("/relationship/searchArticle");
        List<Policy_analysis> analysisList = policy_analysisService.getByPolicyBased(policyId);
        Policy policy = policyService.findById(policyId);
        List<Policy_content> contentList = policy_contentService.getById(policyId);
        mv.addObject("analysisList", analysisList);
        mv.addObject("contentList", contentList);
        mv.addObject("policy", policy);
        List<EsPolicy> esAnalysisList = esPolicyRepository.findByAnalysisNameLikeAndPolicyIdEquals(analysisName, policyId);
        System.out.println(esAnalysisList);
        mv.addObject("esAnalysisList", esAnalysisList);
        return mv;
    }

    @RequestMapping(value = "/geteffectiveness")
    public ModelAndView geteffectiveness(Integer policyId) {

        ModelAndView mv = new ModelAndView("/relationship/graph_echarts");
        Policy p = policyService.findById(policyId);
        List<Policy_analysis> coreSpiritList = policy_analysisService.getByPolicyAndType(p.getPolicyId(), 10);
        if (null != p) {
            if (p.getPolicyGrade() == 1) {
                Map<String, List<Policy>> map = new HashMap<>();
                List<Policy> policyTypeList = policyService.getByPolicyTypeAndGrade(p.getPolicyType(), 2);
                if (null != policyTypeList) {
                    for (Policy policy : policyTypeList
                            ) {
                        //判断要分析的文档中有没有和查询的文档的核心精神相同的。
                        List<Policy_analysis> analysisList = policy_analysisService.getByPolicyAndType(policy.getPolicyId(), 10);

                        int i = 0;
                        //遍历比较
                        for (Policy_analysis coreAnalysis : coreSpiritList
                                ) {
                            for (Policy_analysis analysis : analysisList
                                    ) {
                                if (coreAnalysis.getAnalysisName().equals(analysis.getAnalysisName())) {
                                    i++;
                                    break;
                                }
                            }
                        }
                        //说明可以算作相关的文档
                        if (i > 0) {
                            //按地区存储,map中存在就修改，没有就添加
                            String provinceId = departmentService.getByDepartmentId(userService.getById(policy.getUserId()).getDepartmentId()).getProvinceId();
                            if (!map.isEmpty()) {
                                int j = 0;
                                for (Map.Entry<String, List<Policy>> entry : map.entrySet()) {
                                    //如果找到相同的地区id，添加到list中
                                    if (entry.getKey().equals(provinceId)) {
                                        entry.getValue().add(policy);
                                        j++;
                                        break;
                                    }
                                }
                                if (j == 0) {
                                    List<Policy> policyList = new ArrayList<>();
                                    policyList.add(policy);
                                    map.put(provinceId, policyList);
                                }
                            } else {
                                List<Policy> policyList = new ArrayList<>();
                                policyList.add(policy);
                                map.put(provinceId, policyList);
                            }
                        }
                    }
                    if (!map.isEmpty()) {
                        mv.addObject("map", map);
                        mv.addObject("status", 1);
                        mv.addObject("policyId", policyId);
                        mv.addObject("policyName", p.getPolicyName().replaceAll("doc", "").replaceAll("docx", ""));
                        Date startDate = p.getStartDate();
                        if (null == startDate) {
                            List<ExecuteAndAbolish> executeAndAbolishList = executeAndAbolishService.findByPolicyId(policyId);
                            for (ExecuteAndAbolish executeAndAbolish : executeAndAbolishList
                                    ) {
                                if (null != executeAndAbolish.getStartDate()) {
                                    startDate = executeAndAbolish.getStartDate();
                                    break;
                                }
                            }
                        }
                        if (null != startDate) {
                            mv.addObject("startDate", startDate);
                        }

                    }
                } else {
                    mv.addObject("msg", 3);//下级政策中没有已执行政策
                }
            } else {
                mv.addObject("msg", 2);//等级不符合要求
            }
        } else {
            mv.addObject("msg", 1);//不存在该篇文档
        }

        return mv;
    }

    /**
     * ajax获取历史政策
     *
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/getHistoryPolicy")
    public ModelAndView getHistoryPolicy(Integer policyId) {
        List<Policy> list = new ArrayList<>();
        Policy policy = policyService.findById(policyId);
        ModelAndView mv = new ModelAndView("/relationship/timeline_v2");
        if (null != policy) {
            getSuccessor(policy, list);
            int houji = list.size();
            list.add(policy);
            getprecursor(policy, list);
            int qianqu = list.size() - houji - 1;
            mv.addObject("policyList", list);
            mv.addObject("curr", houji);
            mv.addObject("policyId", policyId);
            mv.addObject("msg", "对比库中共存在" + qianqu + "条前驱文档," + houji + "条后继文档");
        } else {
            mv.addObject("msg", "对比库中不存在该文档的历史文档。");
        }
        return mv;
    }

    private void getprecursor(Policy policy, List<Policy> list) {
        if (null != policy.getPrecursor()) {
            Policy p = policyService.findById(policy.getPrecursor());
            getprecursor(p, list);
            list.add(p);
        }
    }

    private void getSuccessor(Policy policy, List<Policy> list) {
        if (null != policy.getSuccessor()) {
            Policy p = policyService.findById(policy.getSuccessor());
            getSuccessor(p, list);
            list.add(p);
        }
    }


    /**
     * 获取所有数据来加载搜索建议
     *
     * @return
     */
    @RequestMapping(value = "/getAll")
    public List<Policy> getAll() {

        List<Policy> list = policyService.findAll();
        return list;
    }

    /**
     * 根据id获取政策信息
     *
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/getById")
    public Policy getById(@RequestParam(value = "policyId") Integer policyId) {
        Policy p = policyService.findById(policyId);
        return p;
    }

    /**
     * 根据id获取段落信息
     *
     * @param contentId
     * @return
     */
    @RequestMapping(value = "/getContentById")
    public Policy_content getContentById(@RequestParam(value = "contentId") Integer contentId) {
        Policy_content p = policy_contentService.getByContentId(contentId);
        return p;
    }


    /**
     * 多文件上传
     *
     * @param files
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(HttpServletRequest request, @RequestParam(required = false, value = "file[]") MultipartFile[] files) {
        User user = (User) request.getSession().getAttribute("existUser");
        if (null == user) {
            return "noUser";
        }
        BufferedOutputStream stream = null;
        String path = "F:/policy";
        File uploadLocation = new File(path);
        String key = "";
        if (!uploadLocation.exists()) {
            uploadLocation.mkdirs();
        }
        if (files.length > 0) {
            List<Policy> list = new ArrayList<>();

            for (int i = 0; i < files.length; ++i) {
                MultipartFile file = files[i];
                if (!files[i].isEmpty()) {
                    String filePath = path + "/" + file.getOriginalFilename();
                    File existFile = new File(filePath);
                    if (!existFile.exists()) {
                        try {
                            byte[] bytes = file.getBytes();
                            stream = new BufferedOutputStream(new FileOutputStream(
                                    new File(filePath)));
                            stream.write(bytes);
                            stream.close();


                            Policy p = new Policy();
                            p.setPolicyName(file.getOriginalFilename());
                            p.setStatus(0);
                            p.setUploadDate(new Date());
                            p.setUrl(filePath);
                            if (null != user) {
                                p.setUserId(user.getUserId());
                                System.out.println("设置成功");
                            }
                            list.add(p);


                        } catch (Exception e) {
                            stream = null;
                            return "failed";
                        }
                    } else {
                        // return "exist";
                    }
                } else {
                    //return "empty";
                }
            }
            key = policyService.insertListAndGetKey(list);
        }

        return key;
    }

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @RequestMapping("/updatePolicyInfo")
    public ModelAndView updatePolicyInfo(HttpServletRequest request, Integer policyId, Integer policyType, String startDate, String endDate, Integer precursorId, Integer successorId) {

        User user = (User) request.getSession().getAttribute("existUser");
        if (null != user) {
            Policy p = policyService.findById(policyId);
            if (null != policyType) {
                p.setPolicyType(policyType);
            } else {
                p.setPolicyType(null);
            }
            if (null != startDate && startDate.length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    p.setStartDate(sdf.parse(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                p.setStartDate(null);
            }
            if (null != endDate && endDate.length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    p.setEndDate(sdf.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                p.setEndDate(null);
            }
            if (null != precursorId) {
                p.setPrecursor(precursorId);

            } else {
                p.setPrecursor(null);
            }
            if (null != successorId) {
                p.setSuccessor(successorId);
            } else {
                p.setSuccessor(null);
            }

            policyService.update(p);
            policyService.updateStatus(policyId, 2);
            p.setPolicyGrade(departmentService.getByDepartmentId(user.getDepartmentId()).getGrade());
            ModelAndView mv = new ModelAndView("redirect:/policy/list?msg=2");
            return mv;
        } else {
            System.out.println("session失效");
            request.getSession().removeAttribute("existUser");
            request.getSession().invalidate();
            return new ModelAndView("redirect:/page/login?msg=2");
        }
    }

    @RequestMapping("/setTypeInfo")
    public ModelAndView setTypeInfo(@RequestParam(value = "policyId") Integer policyId) {
        ModelAndView mv = new ModelAndView("/policy/policy_info");
        List<Policy_type> policy_types = policy_typeService.getAll();
        Policy policy = policyService.findById(policyId);
        mv.addObject("policy_types", policy_types);
        mv.addObject("policy", policy);
        List<Department> list = departmentService.getAll();
        int mingrade = 20;
        int maxgrade = 0;
        for (Department d : list
                ) {
            if (d.getGrade() > maxgrade) {
                maxgrade = d.getGrade();
            }
            if (d.getGrade() < mingrade) {
                mingrade = d.getGrade();
            }
        }
        List<Integer> list1 = new ArrayList<>();
        for (int i = mingrade; i <= maxgrade; i++) {
            list1.add(i);
        }
        if (list1 != null) {
            mv.addObject("grades", list1);
        }
        List<ExecuteAndAbolish> executeAndAbolishList = executeAndAbolishService.findByPolicyId(policyId);
        if (executeAndAbolishList.size() == 0 && policy.getStartDate() == null && policy.getEndDate() == null) {
            mv.addObject("executeAndAbolish", 1);//说明没有分析出施行日期
        }

        return mv;
    }

    /**
     * 分页展示政策列表
     *
     * @param pageCode
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, PolicyCondition policyCondition, String msg) {
        PageInfo<Policy> list = policyService.findByPage(pageCode, pageSize, policyCondition);
        ModelAndView mv = new ModelAndView("/policy/table_basic", "page", list);
        mv.addObject("policyCondition", policyCondition);
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }

    @RequestMapping(value = "/getFileById")
    public ModelAndView getFileById(@RequestParam(value = "policyId") Integer policyId) {

        /**
         * 先查询该文档是否被读取，没读取需要先读取。
         */
        Integer count = policy_contentService.getCount(policyId);
        List<Policy_content> list = new ArrayList<>();
        if (count == 0) {
            Policy p = policyService.findById(policyId);
            if (null != p && null != p.getUrl()) {
                solveFileByUrl(p.getUrl(), policyId);
                list = policy_contentService.getById(policyId);
            }

        } else {
            list = policy_contentService.getById(policyId);
        }
        return new ModelAndView("/policy/article", "contents", list);
    }

    @RequestMapping(value = "/delete")
    public ModelAndView delete(@RequestParam(value = "policyId") Integer policyId) {
        // policyService.deleteById(policyId);
        //policyService.updateStatus(policyId,3);
        return new ModelAndView("redirect:/policy/list?msg=1");
    }

    /**
     * 根据url和政策id读取政策内容，同时解决标题有换行符问题
     *
     * @param url
     * @param policyId
     * @return
     */
    private void solveFileByUrl(String url, Integer policyId) {
        List<Policy_content> list = new ArrayList<>();
        File file = new File(url);
        if (file.exists()) {
            try {
                InputStream is = new FileInputStream(file);
                if (url.endsWith(".doc")) {
                    POIFSFileSystem fs = new POIFSFileSystem(is);
                    HWPFDocument document = new HWPFDocument(fs);
                    Range range = document.getRange();

                    CharacterRun run1 = null;//用来存储第一行内容的属性
                    CharacterRun run2 = null;//用来存储第二行内容的属性

                    for (int i = 0; i < range.numParagraphs(); i++) {
                        Paragraph para = range.getParagraph(i);// 获取第i段

                        String paratext = para.text().trim().replaceAll("\r\n", "");
                        if (paratext.length() > 0) {
                            //需要判断上一行元素是否和该行元素是否是一部分内容，主要是处理标题,上一行内容为空的不考虑。
                            if (i > 0 && para.getCharacterRun(0).isBold() == true && range.getParagraph(i - 1).text().trim().replaceAll("\r\n", "").length() > 0) {
                                run1 = para.getCharacterRun(0);
                                run2 = range.getParagraph(i - 1).getCharacterRun(0);

                                if (null != run1 || null != run2) {
                                    String contentText = list.get(list.size() - 1).getContentText();
                                    if (run1.getFontSize() == run2.getFontSize() && run1.isBold() == run2.isBold() && run1.isItalic() == run2.isItalic()) {
                                        list.get(list.size() - 1).setContentText(contentText + paratext);
                                        continue;
                                    }
                                }
                            }

                            if (para.isInTable() || para.isTableRowEnd()) {
                                continue;
                            }

                            Policy_content policy_content = new Policy_content();
                            policy_content.setContentText(paratext);
                            policy_content.setPolicyId(policyId);
                            policy_content.setLocation(para.getJustification());
                            policy_content.setSummary(getContentSummary(paratext));
                            list.add(policy_content);
                        }

                    }
                } else if (url.endsWith(".docx")) {
                    XWPFDocument doc = new XWPFDocument(is);
                    List<XWPFParagraph> paras = doc.getParagraphs();
                    XWPFRun run1 = null;
                    XWPFRun run2 = null;
                    for (int i = 0; i < paras.size(); i++) {
                        XWPFParagraph paragraph = paras.get(i);
                        String paratext = paragraph.getText().trim().replaceAll("\r\n", "");

                        if (paratext.length() > 0) {
                            //需要判断上一行元素是否和该行元素是否是一部分内容，主要是处理标题

                            if (i > 0 && paragraph.getRuns().get(0).isBold() == true && paras.get(i - 1).getText().trim().replaceAll("\r\n", "").length() > 0) {
                                run1 = paragraph.getRuns().get(0);
                                run2 = paras.get(i - 1).getRuns().get(0);
                                if (null != run1 || null != run2) {
                                    String contentText = list.get(list.size() - 1).getContentText();
                                    if (run1.getFontSize() == run2.getFontSize() && run1.isBold() == run2.isBold() && run1.isItalic() == run2.isItalic()) {
                                        list.get(list.size() - 1).setContentText(contentText + paratext);
                                        continue;
                                    }
                                }
                            }

                            Policy_content policy_content = new Policy_content();
                            policy_content.setContentText(paratext);
                            policy_content.setPolicyId(policyId);
                            policy_content.setSummary(getContentSummary(paratext));
                            String alignment = paragraph.getAlignment().toString();
                            if ("LEFT".equals(alignment)) {
                                policy_content.setLocation(0);
                            } else if ("CENTER".equals(alignment)) {
                                policy_content.setLocation(1);
                            } else if ("RIGHT".equals(alignment)) {
                                policy_content.setLocation(2);
                            }
                            list.add(policy_content);
                        }

                    }
                }
                is.close();
                policy_contentService.insertList(list);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取段落的摘要
     *
     * @param contentText
     * @return
     */
    private static String getContentSummary(String contentText) {
        String result = "";
        List<String> stringList = HanLP.extractPhrase(contentText, StringUtil.getKeySizeByLength(contentText));
        int i = 0;
        for (String s : stringList
                ) {
            if (i == 0) {
                result = s;
                i++;
            } else {
                result += "," + s;
            }
        }
        return result;

    }

}
