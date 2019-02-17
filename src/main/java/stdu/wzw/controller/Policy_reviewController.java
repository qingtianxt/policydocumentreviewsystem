package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import com.hankcs.hanlp.HanLP;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.condition.PolicyCondition;
import stdu.wzw.condition.Policy_compareCondition;
import stdu.wzw.mapper.Policy_reviewMapper;
import stdu.wzw.model.*;
import stdu.wzw.service.*;
import stdu.wzw.utils.MySimHash;
import stdu.wzw.utils.SentenceTypeAnalysis;
import stdu.wzw.utils.SimilarityAnalysisUtils;
import stdu.wzw.utils.StringUtil;
import stdu.wzw.utils.sentenceSimilarity.Segment;
import stdu.wzw.utils.sentenceSimilarity.Word2Vec;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping(value = {"/review"})
public class Policy_reviewController {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Policy_reviewController.class);

    @Autowired
    private PolicyService policyService;
    @Autowired
    private Policy_reviewService policy_reviewService;
    @Autowired
    private Policy_review_contentService policy_review_contentService;
    @Autowired
    private Policy_review_analysisService policy_review_analysisService;
    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private Policy_analysisService policy_analysisService;
    @Autowired
    private Policy_similarityService policy_similarityService;
    @Autowired
    private Policy_contentService policy_contentService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private LegitimacyanalysisService legitimacyanalysisService;
    @Autowired
    private UserService userService;
    @Autowired
    private Policy_compare_ULrelationshipService uLrelationshipService;

    /**
     * 生成word版本的文档。
     *
     * @param reviewId
     * @return
     */
    private ModelAndView makeWordReport(@RequestParam(value = "reviewId") Integer reviewId) {

        return new ModelAndView();
    }


    /**
     * 根据审查文档的id获取和该篇文档相似的文档
     *
     * @param reviewId
     * @return
     */
    @RequestMapping(value = "/getPolicySimilarity")
    public List<Policy_similarity> getPolicySimilarity(@RequestParam(value = "reviewId") Integer reviewId) {
        List<Policy_similarity> list = policy_similarityService.getPolicySimilarity(reviewId);
        return list;
    }


    /**
     * 根据句子id获取相似信息
     *
     * @param analysisId
     * @return
     */
    @RequestMapping(value = "/getSimilarityByAnalysisId")
    public List<Policy_similarity> getSimilarityByAnalysisId(@RequestParam(value = "analysisId") Integer analysisId) {
        List<Policy_similarity> list = policy_similarityService.getByAnalysisIdAndType(analysisId);
        return list;
    }


    //通过审查政策id获取报告信息
    @RequestMapping(value = "/getReport")
    public ModelAndView getReport(@RequestParam(value = "reviewid") Integer reviewId) {
        ModelAndView mv = new ModelAndView("/review/review_report");

        Policy_review policy_review = policy_reviewService.findById(reviewId);
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_review_analysis> list = policy_review_analysisService.getBasedByReviewId(reviewId);
        mv.addObject("policy_review", policy_review);
        mv.addObject("contentList", contentList);
        mv.addObject("list", list);
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
        PageInfo<Policy_review> list = policy_reviewService.findByPage(pageCode, pageSize);
        ModelAndView mv = new ModelAndView("/review/review_list", "page", list);
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }

    /**
     * 多文件上传
     *
     * @param files
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(HttpServletRequest request, @RequestParam(required = false, value = "file[]") MultipartFile[] files, String msg) {

        User user = (User) request.getSession().getAttribute("existUser");
        if (null == user) {
            return "noUser";
        }

        BufferedOutputStream stream = null;
        String path = "";
        if (StringUtil.StringToInt(msg) == 1) {
            path = "F:/review/legitimacyAnalysis";
        } else {
            path = "F:/review";
        }
        File uploadLocation = new File(path);
        String key = "";
        if (!uploadLocation.exists()) {
            logger.info("创建文件夹");
            uploadLocation.mkdirs();
        }
        if (files.length > 0) {
            List<Policy_review> list = new ArrayList<>();

            for (int i = 0; i < files.length; ++i) {
                MultipartFile file = files[i];
                if (!files[i].isEmpty()) {
                    String filePath = path + "/" + file.getOriginalFilename();
                    File existFile = new File(filePath);
                    //不得上传同名的审查文档，后期需要处理，可以考虑按照部门来分类存放文档
                    if (!existFile.exists()) {
                        try {
                            byte[] bytes = file.getBytes();
                            stream = new BufferedOutputStream(new FileOutputStream(
                                    new File(filePath)));
                            stream.write(bytes);
                            stream.close();


                            Policy_review p = new Policy_review();
                            p.setReviewName(file.getOriginalFilename());

                            if (StringUtil.StringToInt(msg) == 1) {
                                p.setType(2);
                            } else {
                                p.setType(1);
                            }
                            p.setUploadDate(new Date());
                            p.setUrl(filePath);

                            if (null != user) {
                                p.setUserId(user.getUserId());
                                System.out.println("设置成功1");
                            }
                            User u = userService.getById(p.getUserId());
                            p.setDepartmentId(u.getDepartmentId());

                            p.setGrade(departmentService.getByDepartmentId(u.getDepartmentId()).getGrade());
                            p.setStatus(0);

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
            key = policy_reviewService.insertListAndGetKey(list);
        }
        logger.info("------------------------");
        logger.info("文件上传完成");
        logger.info("------------------------");
        return key;
    }


    @RequestMapping(value = "/examine")
    public String examine(String keys) throws InterruptedException {
        logger.info("------------------------");
        logger.info("审查开始");
        logger.info("keys:" + keys);
        logger.info("------------------------");

        String[] split = keys.split(",");
        for (String s : split
                ) {
            int review_id = StringUtil.StringToInt(s);
            analysis(review_id);
            analysisLegitimacy(review_id);

            Policy_review policy_review = policy_reviewService.findById(review_id);
            policy_review.setStatus(1);
            logger.info(policy_review);
            policy_reviewService.updateStatus(policy_review);
        }

        // Thread.sleep(5000);
        return "success";
    }


    /**
     * 对比文件，根据文件名
     *
     * @return
     */
    @RequestMapping(value = "/compare")
    public String compare(String keys) throws Exception {
        logger.info("------------------------");
        logger.info("对比开始");
        logger.info("keys:" + keys);
        logger.info("------------------------");

        String[] split = keys.split(",");
        for (String s : split
                ) {
            int review_id = StringUtil.StringToInt(s);
            //1.对文档进行基础分析
            analysis(review_id);
            //2.文档相关性文档分析。
            analysisSimilarity(review_id);
            analysisContentSimlarity(review_id);

            //全文相似度分析
            analysisTextSimlarity(review_id);
            //3.上下级政策分析
            analysisuallevel(review_id);

            //4.同级政策相关性分析。

            Policy_review policy_review = policy_reviewService.findById(review_id);
            policy_review.setStatus(1);
            logger.info(policy_review);
            policy_reviewService.updateStatus(policy_review);
        }

        // Thread.sleep(5000);
        return "success";
    }

    /**
     * 分析上下级关系
     *
     * @param review_id
     */
    private void analysisuallevel(int review_id) {
        Policy_review policy_review = policy_reviewService.findById(review_id);
        List<Policy_similarity> similarityList = policy_similarityService.getPolicySimilarity(review_id);
        List<Policy_review_content> review_contentList = policy_review_contentService.getByReviewId(review_id);
        String reviewContent = "";
        Policy Simliratypolicy = new Policy();//最相似的分类的文档
        //保存对比文档的内容
        for (Policy_review_content content : review_contentList
                ) {
            reviewContent += content.getContentText();
        }
        if (null != similarityList) {

            //增加判断分类的条件，首先验证核心精神,这里认为核心精神相同，分类就相同
            List<Policy_review_analysis> reviewcoreSpiritList = policy_review_analysisService.getByReviewIdAndType(review_id, 10);
            Map<String, List<Policy_analysis>> coreSpireMap = new HashMap<>();
            List<Policy> policyList = policyService.findAll();

            //1.存在核心精神内容相同的情况
            for (Policy policy : policyList
                    ) {
                List<Policy_analysis> coreSpiritList = policy_analysisService.getByPolicyAndType(policy.getPolicyId(), 10);
                for (Policy_analysis analysis : coreSpiritList
                        ) {
                    for (Policy_review_analysis review_analysis : reviewcoreSpiritList
                            ) {
                        //前提条件是对比的两部分内容相同
                        if (analysis.getAnalysisName().equals(review_analysis.getAnalysisName())) {
                            if (coreSpireMap.isEmpty()) {
                                List<Policy_analysis> list = new ArrayList<>();
                                list.add(analysis);
                                coreSpireMap.put(analysis.getAnalysisName(), list);
                            } else {
                                //判断map中是否存在
                                int i = 0;
                                for (String key : coreSpireMap.keySet()) {
                                    if (key.equals(analysis.getAnalysisName())) {
                                        i++;
                                        coreSpireMap.get(key).add(analysis);
                                        break;
                                    }
                                }
                                if (i == 0) {
                                    List<Policy_analysis> list = new ArrayList<>();
                                    list.add(analysis);
                                    coreSpireMap.put(analysis.getAnalysisName(), list);
                                }
                            }
                        }


                    }
                }

            }
            if (!coreSpireMap.isEmpty()) {

                String finalKey = "";
                int maxCount = 0;
                for (Map.Entry<String, List<Policy_analysis>> entry : coreSpireMap.entrySet()) {
                    if (entry.getValue().size() > maxCount) {
                        maxCount = entry.getValue().size();
                        finalKey = entry.getKey();
                    }
                }
                Simliratypolicy = policyService.findById(coreSpireMap.get(finalKey).get(0).getPolicyId());

            } else {
                //2.所有核心精神都不同，判断核心精神，最相似的内容的句子的分类算作对比文档的分类
                Policy_analysis policy_analysis = new Policy_analysis();
                int maxSimilarity = 0;
                for (Policy policy : policyList
                        ) {
                    List<Policy_analysis> coreSpiritList = policy_analysisService.getByPolicyAndType(policy.getPolicyId(), 10);
                    for (Policy_analysis analysis : coreSpiritList
                            ) {
                        for (Policy_review_analysis review_analysis : reviewcoreSpiritList
                                ) {
                            float round = SimilarityAnalysisUtils.analysisByCos(analysis.getAnalysisName(), review_analysis.getAnalysisName());
                            int degree = Math.round(round * 100);
                            if (maxSimilarity < degree) {
                                maxSimilarity = degree;
                                policy_analysis = analysis;
                            }
                        }
                    }
                }
                Simliratypolicy = policyService.findById(policy_analysis.getPolicyId());
            }

            //找相似度最大的内容的分类作为对比文档的分类
            List<Policy> list = policyService.getByPolicyType(Simliratypolicy.getPolicyType());

            //获取对比文档的等级，
            int review_grade = policy_review.getGrade();
            //按地区存储
            List<Policy_compareCondition> list1 = new ArrayList<>();

            //先提取指定分类，分类后再按地区分类。
            for (Policy p : list
                    ) {
                if (review_grade == 1) {
                    for (int i = 1; i <= 2; i++) {
                        if (p.getPolicyGrade() == i) {
                            analysisTypeByPlace(list1, p);
                        }
                    }

                } else if (review_grade == 4) {
                    for (int i = 3; i <= 4; i++) {
                        if (p.getPolicyGrade() == i) {
                            analysisTypeByPlace(list1, p);
                        }
                    }

                } else if (review_grade < 4 && review_grade > 1) {
                    for (int i = review_grade - 1; i <= review_grade + 1; i++) {
                        if (p.getPolicyGrade() == i) {
                            analysisTypeByPlace(list1, p);
                        }

                    }
                }

            }
            //遍历这个分类，然后每个地区中取出一个最相关的数据。
            for (Policy_compareCondition po : list1
                    ) {
                if (po.getPolicyList().size() > 1) {
                    //找到一个最相关的。
                    int maxDegree = 0;
                    Policy policy1 = new Policy();
                    for (Policy p1 : po.getPolicyList()
                            ) {
                        List<Policy_content> contentList = policy_contentService.getById(p1.getPolicyId());
                        String content = "";
                        for (Policy_content c : contentList
                                ) {
                            content += c.getContentText();
                        }
                        //采用SimHash的方式对比两篇文档的相似度，simhash在文字超过500字的时候效果比较好
                        MySimHash mySimHash1 = new MySimHash(reviewContent, 64);
                        MySimHash mySimHash2 = new MySimHash(content, 64);
                        double semblance = mySimHash1.getSemblance(mySimHash2);
                        //将相似度转化为int类型
                        DecimalFormat df = new DecimalFormat("#.00");
                        String replace = df.format(semblance).replace(".", "");
                        int degree = StringUtil.StringToInt(replace);
                        if (maxDegree < degree) {
                            maxDegree = degree;
                            policy1 = p1;
                        }
                    }

                    List<Policy> list2 = new ArrayList<>();
                    list2.add(policy1);
                    po.setPolicyList(list2);
                }

            }

            List<Policy_compare_ULrelationship> lrelationshipsList = new ArrayList<>();
            //遍历完之后，每个地区保存了一个最相关的数据。
            for (Policy_compareCondition p : list1
                    ) {
                Policy_compare_ULrelationship policy_compare_uLrelationship = new Policy_compare_ULrelationship(review_id, p.getPolicyList().get(0).getPolicyId(), p.getPolicyList().get(0).getPolicyGrade(), p.getPlaceId(), 1);
                lrelationshipsList.add(policy_compare_uLrelationship);
            }
            uLrelationshipService.insertList(lrelationshipsList);
        }
    }

    /**
     * 根据地区来分类，服务于分析关系
     *
     * @param list1
     * @param p
     */
    private void analysisTypeByPlace(List<Policy_compareCondition> list1, Policy p) {
        //根据等级先查找出对应的地区id
        String place_id = "";
        Department d = departmentService.getByDepartmentId(userService.getById(p.getUserId()).getDepartmentId());
        if (p.getPolicyGrade() <= 2) {
            place_id = d.getProvinceId();
        } else if (p.getPolicyGrade() == 3) {
            place_id = d.getCityId();
        } else if (p.getPolicyGrade() == 4) {
            place_id = d.getAreaId();
        }
        if (list1.size() == 0) {
            Policy_compareCondition policyCompareCondition = new Policy_compareCondition();
            policyCompareCondition.setPlaceId(place_id);
            List<Policy> list2 = new ArrayList<>();
            list2.add(p);
            policyCompareCondition.setPolicyList(list2);
            list1.add(policyCompareCondition);
        } else {

            //判断p的地区是否已经存在list1中。
            int i = 0;
            for (Policy_compareCondition po : list1
                    ) {
                if (po.getPlaceId().equals(place_id)) {
                    i++;
                    po.getPolicyList().add(p);
                    break;
                }

            }
            if (i == 0) {
                Policy_compareCondition policyCompareCondition = new Policy_compareCondition();
                policyCompareCondition.setPlaceId(place_id);
                List<Policy> list2 = new ArrayList<>();
                list2.add(p);
                policyCompareCondition.setPolicyList(list2);
                list1.add(policyCompareCondition);
            }
        }
    }


    @RequestMapping(value = "/delete")
    public ModelAndView delete(@RequestParam(value = "policyId") Integer policyId) {
        policyService.deleteById(policyId);
        return new ModelAndView("redirect:/policy/list?msg=1");
    }


    /**
     * 获取文档的段落内容
     *
     * @param reviewId
     * @return
     */
    @RequestMapping(value = "/getContent")
    public ModelAndView getContent(@RequestParam(value = "reviewid") Integer reviewId) {

        List<Policy_review_content> list = getContentByReviewId(reviewId);
        return new ModelAndView("/review/review_article", "contents", list);
    }

    /**
     * 分析句子合法性
     *
     * @param reviewId
     */
    private void analysisLegitimacy(Integer reviewId) {
        List<Policy_review_analysis> policy_reviewList = policy_review_analysisService.getBasedByReviewId(reviewId);
        int totalResult = 100;
        int degree = 100;
        List<SensitiveWord> sensitiveWordList = sensitiveService.findAll();
        List<Legitimacy_analysis> list = new ArrayList<>();
        String forbiddenwords = "";
        boolean[] ifcontents = {false, false, false, false};


        for (Policy_review_analysis analysis : policy_reviewList
                ) {
            //每次需要重置合法性值和禁用词
            int enddegree = 100;
            degree = 100;
            forbiddenwords = "";
            System.out.println(analysis);
            int i = 0;
            String analysisName = analysis.getAnalysisName();

            //判断是否有禁用词
            for (SensitiveWord sensitiveWord : sensitiveWordList
                    ) {
                if (analysisName.contains(sensitiveWord.getSensitiveName()) && sensitiveWord.getType() == 2) {
                    degree = 30;
                    if (i == 0) {
                        forbiddenwords += sensitiveWord.getSensitiveName();
                        i++;
                    } else {
                        forbiddenwords += "," + sensitiveWord.getSensitiveName();
                    }

                }
            }
            if (i > 0) {
                String info = "该句中包含禁用词:" + forbiddenwords;
                degree = 30;
                Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 5, info, reviewId, 30);
                list.add(legitimacy_analysis);
            }
            if (totalResult >= degree) {
                totalResult = degree;
            }
            if (enddegree >= degree) {
                enddegree = degree;
            }

            //判断总标题名称是否包含不合法词汇。
            if (analysis.getType() == 1) {
                if (analysisName.contains("条例") || analysisName.contains("批复") || analysisName.contains("报告")) {
                    degree = 30;
                    String info = "该句中包含不合法名称:";
                    int j = 0;
                    if (analysisName.contains("条例")) {
                        if (j == 0) {
                            info += "条例";
                            j++;
                        } else {
                            info += ",条例";
                        }
                    }
                    if (analysisName.contains("批复")) {
                        if (j == 0) {
                            info += "批复";
                            j++;
                        } else {
                            info += ",批复";
                        }
                    }
                    if (analysisName.contains("报告")) {
                        if (j == 0) {
                            info += "报告";
                            j++;
                        } else {
                            info += ",报告";
                        }
                    }
                    if (j == 0) {
                        info = "系统分析错误";
                    }
                    Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 1, info, reviewId, 30);
                    list.add(legitimacy_analysis);

                    if (totalResult >= degree) {
                        totalResult = degree;
                    }
                }
            }//判断是否在对实施日期、有关专门术语以及与旧规范的关系等内容作出规定， 一般不对权利与义务作出规定。
            else if (analysis.getType() == 6) {
                if (analysisName.contains("权利") || analysisName.contains("义务")) {
                    degree = 50;
                    String info = "该句中包含不合理词汇:";
                    int j = 0;
                    if (analysisName.contains("权利")) {
                        if (j == 0) {
                            info += "权利";
                            j++;
                        } else {
                            info += ",权利";
                        }
                    }
                    if (analysisName.contains("义务")) {
                        if (j == 0) {
                            info += "义务";
                            j++;
                        } else {
                            info += ",义务";
                        }
                    }
                    if (j == 0) {
                        info = "系统分析错误";
                    }
                    Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 4, info, reviewId, 50);
                    list.add(legitimacy_analysis);
                    if (totalResult >= degree) {
                        totalResult = degree;
                    }
                    if (enddegree >= degree) {
                        enddegree = degree;
                    }
                }
            } else {
                //不包含行政处罚、行政许可、行政审批、行政强制、行政事业性收费、机构编制以及其他不得由规范性文件创设的事项
                String[] type2 = {"行政处罚", "行政许可", "行政审批", "行政强制", "行政事业性收费", "机构编制"};
                int j = 0;
                String info = "该句中包含不合法词汇:";
                for (String content : type2
                        ) {
                    if (analysisName.contains(content)) {

                        degree = 30;
                        if (j == 0) {
                            j++;
                            info += content;
                        } else {
                            info += "," + content;
                        }
                    }
                }
                if (j > 0) {
                    Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 2, info, reviewId, 30);
                    list.add(legitimacy_analysis);
                }
                if (totalResult >= degree) {
                    totalResult = degree;
                }
                if (enddegree >= degree) {
                    enddegree = degree;
                }
                //内容中最好不采用补充通知，补充规定等名称
                String[] type3 = {"补充通知", "补充规定"};
                int k = 0;
                String info1 = "该句中包含不合理词汇:";
                for (String content : type3
                        ) {
                    if (analysisName.contains(content)) {

                        degree = 50;
                        if (k == 0) {
                            k++;
                            info1 += content;
                        } else {
                            info1 += "," + content;
                        }
                    }
                }
                if (k > 0) {
                    Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 3, info1, reviewId, 50);
                    list.add(legitimacy_analysis);
                }
                if (totalResult >= degree) {
                    totalResult = degree;
                }
                if (enddegree >= degree) {
                    enddegree = degree;
                }
            }
            //说明是正常合法句子
            if (degree == 100) {
                degree = 70;
                Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(analysis.getAnalysisId(), 6, "该句未检测出不合法情况", reviewId, 70);
                list.add(legitimacy_analysis);
            }

            if (totalResult >= degree) {
                totalResult = degree;
            }
            if (enddegree >= degree) {
                enddegree = degree;
            }
            //判断是否包含适用范围、适用主体、主要措施、施行日期等内容
            if (ifcontents[0] == false) {
                if (analysisName.contains("适用范围")) {
                    ifcontents[0] = true;
                }
            }
            if (ifcontents[1] == false) {
                if (analysisName.contains("适用主体")) {
                    ifcontents[1] = true;
                }
            }
            if (ifcontents[2] == false) {
                if (analysisName.contains("主要措施")) {
                    ifcontents[2] = true;
                }
            }
            if (ifcontents[3] == false) {
                if (analysis.getType() == 6) {
                    ifcontents[3] = true;
                }
            }

            System.out.println(enddegree);
            analysis.setLegitimacy(enddegree);
        }

        policy_review_analysisService.updateList(policy_reviewList);

        //统计文档整体合法性。
        //目前没有用户,后期添加,暂时不添加超越职权判断。
        String resultInfo = "";
        if (totalResult == 30) {
            resultInfo = "本文档中包含不合法句子。";
        } else if (totalResult == 50) {
            resultInfo = "本文档中包含不合理句子。";
        } else {
            int k = 0;
            resultInfo = "本句子中包含规范性文件主体元素：";
            if (ifcontents[0] == true) {
                resultInfo += "适用范围";
                totalResult += 5;
            }
            if (ifcontents[1] == true) {
                resultInfo += "适用主体";
                totalResult += 5;
            }
            if (ifcontents[2] == true) {
                resultInfo += "主要措施";
                totalResult += 5;
            }
            if (ifcontents[3] == true) {
                resultInfo += "施行日期";
                totalResult += 5;
            }
            if (totalResult == 70) {
                resultInfo = "文档中不包含不合法的句子或标题。";
            }
        }
        //更新数据
        Policy_review policy_review = policy_reviewService.findById(reviewId);

        logger.info("------------更新句子信息完成-----------");
        policy_review.setLegitimacydegree(totalResult);
        policy_reviewService.update(policy_review);
        logger.info("------------更新文档信息完成-----------");

        Legitimacy_analysis legitimacy_analysis = new Legitimacy_analysis(8, resultInfo, reviewId, totalResult);
        list.add(legitimacy_analysis);
        legitimacyanalysisService.insertList(list);
        logger.info("------------插入句子和文档合法性信息完成-----------");
    }

    /**
     * 整篇文档的相似性分析
     *
     * @param review_id
     */
    private void analysisTextSimlarity(int review_id) {
        //1.获取审查内容，存储在字符串中
        String reviewContent = "";
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(review_id);
        List<Policy_similarity> policy_similarityList = new ArrayList<>();
        if (null != contentList && contentList.size() > 0) {
            for (Policy_review_content p : contentList
                    ) {
                reviewContent += p.getContentText();
            }

            //2.获取所有对比库中的政策，然后遍历获取内容存储在字符串
            List<Policy> policyList = policyService.findAll();
            if (null != policyList && policyList.size() > 0) {
                for (Policy p1 : policyList
                        ) {

                    //获取政策段内容，存储在字符串
                    String content = "";
                    List<Policy_content> policy_contents = policy_contentService.getById(p1.getPolicyId());
                    if (null != policy_contents && policy_contents.size() > 0) {
                        for (Policy_content p2 : policy_contents
                                ) {
                            content += p2.getContentText();
                        }
                    }
                    //如果内容获取不到，说明没有分析，在这需要进行文档内容的获取
                    if (null == content || content.length() == 0) {
                        solveFileByUrl1(p1.getUrl(), p1.getPolicyId());
                    }
                    //采用SimHash的方式对比两篇文档的相似度，simhash在文字超过500字的时候效果比较好
                    MySimHash mySimHash1 = new MySimHash(reviewContent, 64);
                    MySimHash mySimHash2 = new MySimHash(content, 64);
                    double semblance = mySimHash1.getSemblance(mySimHash2);
                    //将相似度转化为int类型
                    DecimalFormat df = new DecimalFormat("#.00");
                    String replace = df.format(semblance).replace(".", "");
                    int degree = StringUtil.StringToInt(replace);

                    Policy_similarity policy_similarity = new Policy_similarity();
                    policy_similarity.setReviewId(review_id);
                    policy_similarity.setType(2);
                    policy_similarity.setSimilarityPolicyId(p1.getPolicyId());
                    policy_similarity.setSimilarityDegree(degree);

                    policy_similarityList.add(policy_similarity);
                }

                //对列表按照相似度大小排序，并选取相似度靠前的十条
                Collections.sort(policy_similarityList, new SortByDegree());
                if (policy_similarityList.size() > 10) {
                    policy_similarityList = policy_similarityList.subList(0, 5);
                }
                policy_similarityService.insertList(policy_similarityList);
                int similarityDegree = policy_similarityList.get(0).getSimilarityDegree();
                if (similarityDegree > 0) {
                    Policy_review policy_review = policy_reviewService.findById(review_id);
                    policy_review.setSimilarityDegree(similarityDegree);
                    policy_reviewService.update(policy_review);
                }

            }

        }

    }

    /**
     * 段落级别的相似性分析
     *
     * @param review_id
     */
    private void analysisContentSimlarity(Integer review_id) {
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(review_id);
        List<Policy_similarity> policy_similarityList = new ArrayList<>();
        List<Policy_content> policy_contents = policy_contentService.getAll();
        List<Policy_review_content> contentList1 = new ArrayList<>();

        if (null != contentList && contentList.size() > 0) {
            for (Policy_review_content content : contentList
                    ) {

                List<Policy_similarity> policy_similarityList1 = new ArrayList<>();
                for (Policy_content policy_content : policy_contents
                        ) {
                    //获取两个句子的相似度
                    float degree = SimilarityAnalysisUtils.analysisByCos(content.getContentText(), policy_content.getContentText());
                    // DecimalFormat df = new DecimalFormat("0%");
                    int round = Math.round(degree * 100);
                    if (round == 0) {
                        continue;
                    }

                    Policy_similarity p = new Policy_similarity();

                    p.setSimilarityDegree(round);
                    p.setSimilarityPolicyId(policy_content.getPolicyId());
                    p.setSimilarityContentId(policy_content.getContentId());

                    p.setReviewId(review_id);
                    p.setReviewContentId(content.getContentId());
                    p.setType(3);

                    policy_similarityList1.add(p);
                }
                //对列表按照相似度大小排序，并选取相似度靠前的五条
                Collections.sort(policy_similarityList1, new SortByDegree());
                if (policy_similarityList1.size() > 5) {
                    policy_similarityList1 = policy_similarityList1.subList(0, 5);
                }
                policy_similarityList.addAll(policy_similarityList1);

                //在审查分析段落表中设置相似度的值
                if (policy_similarityList1.size() > 0) {
                    Integer similarityDegree = policy_similarityList1.get(0).getSimilarityDegree();
                    content.setSimilarityDegree(similarityDegree);
                    contentList1.add(content);
                }
            }
            policy_similarityService.insertList(policy_similarityList);
            policy_review_contentService.updateList(contentList1);
        }
    }

    /**
     * 句子级别的相似性分析
     *
     * @param reviewId
     */
    private void analysisSimilarity(Integer reviewId) throws Exception {
        logger.info("文档条款相似度分析开始");
        Word2Vec vec = new Word2Vec();
        try {
            //vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
            long startDaoru = System.currentTimeMillis();
            System.out.println("开始导入");
            vec.loadGoogleModel("G:\\就业资料\\spring boot\\Google_word2vec_zhwiki1710_300d.bin");
            long endDaoru = System.currentTimeMillis();
            System.out.println((endDaoru - startDaoru) + "ms");
            System.out.println("导入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Policy_analysis> list = new ArrayList<>();
        List<Policy> policyList = policyService.findAll();
        for (Policy policy : policyList
                ) {
            List<Policy_analysis> analysisList = policy_analysisService.getByPolicyBased(policy.getPolicyId());
            list.addAll(analysisList);
        }

        List<Policy_review_analysis> list1 = policy_review_analysisService.getBasedByReviewId(reviewId);


        List<Policy_similarity> policy_similarityList = new ArrayList<>();
        List<Policy_review_analysis> policy_review_analysisList = new ArrayList<>();
        //相似度句子级分析
        //遍历待审查的分析表
        for (Policy_review_analysis policy_review_analysis : list1
                ) {
            int j = 0;
            List<Policy_similarity> policy_similarityList1 = new ArrayList<>();
            //遍历待审查的对比库分析表，进行句子间的分析
            for (Policy_analysis policy_analysis : list
                    ) {
                //获取两个句子的相似度
                if (policy_review_analysis.getType() < 8 || policy_review_analysis.getType() > 9) {
                    continue;
                }
                if (policy_analysis.getType() < 8 || policy_analysis.getType() > 9) {
                    continue;
                }
                float degree = SimilarityAnalysisUtils.semanticsSimilarity(vec, policy_review_analysis.getAnalysisName(), policy_analysis.getAnalysisName());
                // float degree = SimilarityAnalysisUtils.analysisByCos(policy_review_analysis.getAnalysisName(), policy_analysis.getAnalysisName());
                // DecimalFormat df = new DecimalFormat("0%");
                int round = Math.round(degree * 100);
                System.out.println("相似度：" + round);
                Policy_similarity p = new Policy_similarity();

                p.setSimilarityDegree(round);
                p.setSimilarityPolicyId(policy_analysis.getPolicyId());
                p.setSimilarityContentId(policy_analysis.getContentId());
                p.setSimilarityAnalysisId(policy_analysis.getAnalysisId());

                p.setReviewId(reviewId);
                p.setReviewContentId(policy_review_analysis.getContentId());
                p.setReviewAnanlysisId(policy_review_analysis.getAnalysisId());
                p.setType(1);

                policy_similarityList1.add(p);
                j++;
            }
            //说明循环时存储了数据
            if (j > 0) {
                Collections.sort(policy_similarityList1, new SortByDegree());
                if (policy_similarityList1.size() >= 5) {
                    policy_similarityList1 = policy_similarityList1.subList(0, 5);
                }

                logger.info(" -----------------------");
                logger.info(policy_similarityList1);
                logger.info("-----------------------");
                //policy_similarityList用来存储相似类，满一千个存储一次，然后初始化,循环之外，如果列表中还有数据，那就将数据存储起来
                policy_similarityList.addAll(policy_similarityList1);
                if (policy_similarityList.size() > 1000) {
                    policy_similarityService.insertList(policy_similarityList);
                    policy_similarityList = new ArrayList<>();
                }

                //在审查分析句子表中设置相似度的值
                Integer similarityDegree = policy_similarityList1.get(0).getSimilarityDegree();
                if (similarityDegree > 0) {
                    policy_review_analysis.setSimilarityDegree(similarityDegree);
                }

                policy_review_analysisList.add(policy_review_analysis);
                if (policy_review_analysisList.size() > 1000) {
                    policy_review_analysisService.updateList(policy_review_analysisList);
                    policy_review_analysisList = new ArrayList<>();
                }
            }

        }
        //如果policy_similarityList里面还有数据，说明不够一千，这部分数据需要单独存储。
        if (policy_similarityList.size() > 0) {
            policy_similarityService.insertList(policy_similarityList);
        }
        //如果policy_review_analysisList里面还有数据，说明不够一千，这部分数据需要单独存储。
        if (policy_review_analysisList.size() > 0) {
            policy_review_analysisService.updateList(policy_review_analysisList);
        }

        logger.info("------------句子相似度分析完成--------------");
    }

    /**
     * 分析关键词,并返回该文件是否被分析
     *
     * @param reviewId
     * @return
     */
    private boolean analysis(Integer reviewId) {

        boolean ifanalysis = false;
        Integer countAnalysis = policy_review_analysisService.getCountByReviewId(reviewId);
        logger.info("countAnalysis:" + countAnalysis);

        if (countAnalysis == 0) {
            List<Policy_review_analysis> analysisList = new ArrayList<>();
            //最先要判断的是该文档有没有被分析，如果已经被分析则直接获取分析结果展示在页面上

            Policy_review review = policy_reviewService.findById(reviewId);
            //先判断文档有没有分段，没有分段的文档需要先分段
            Integer contentCount = policy_review_contentService.getCount(reviewId);
            //count等于0.先分段
            if (contentCount == 0) {
                solveFileByUrl(review.getUrl(), reviewId);
            }
            String content_text = "";
            //分段，分析关键词等内容，包含在段落分解中(因为要分析句子的格式，必须重新读取文件分段)
            //获取段落内容，查看标题是否是两句话合并而来
            List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
            //获取敏感词
            List<SensitiveWord> sensitiveWordList = sensitiveService.findAll();
            //获取部门
            List<Department> departmentList = departmentService.getAll();
            //获取地址
            List<String> allPlace = getAllPlace();
            //记录总标题的内容
            Policy_review_analysis policy_analysis = new Policy_review_analysis();
            //判断有几种字体，用来识别是不是总标题
            boolean fontSizeType = false;


            String wenzhai = "";
            String url = review.getUrl();

            File file = new File(url);
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    int j = 0;//存储contentlist中的下标。

                    if (url.endsWith(".doc")) {
                        POIFSFileSystem fs = new POIFSFileSystem(is);
                        HWPFDocument document = new HWPFDocument(fs);
                        Range range = document.getRange();

                        CharacterRun run1 = null;//用来存储第一行内容的属性
                        CharacterRun run2 = null;//用来存储第二行内容的属性

                        //判断待分析的是不是最后一个段落
                        int lastSentence = 0;
                        CharacterRun runTitle = null;

                        for (int i = 0; i < range.numParagraphs(); i++) {
                            Paragraph para = range.getParagraph(i);// 获取第i段
                            String paratext = para.text().trim().replaceAll("\r\n", "");

                            if (paratext.length() > 0) {

                                if (para.isInTable() || para.isTableRowEnd()) {
                                    continue;
                                }
                                run1 = para.getCharacterRun(0);
                                //处理多行标题
                                if (i > 0 && para.getCharacterRun(0).isBold() == true && range.getParagraph(i - 1).text().trim().replaceAll("\r\n", "").length() > 0) {
                                    run2 = range.getParagraph(i - 1).getCharacterRun(0);

                                    if (null != run1 && null != run2) {
                                        if (run1.getFontSize() == run2.getFontSize() && run1.isBold() == run2.isBold() && run1.isItalic() == run2.isItalic()) {
                                            analysisList.get(analysisList.size() - 1).setAnalysisName(contentList.get(j - 1).getContentText());
                                            continue;
                                        }
                                    }
                                }

                                wenzhai += paratext;
                                //句子拆分
                                String[] split = paratext.split("。|？|\\?|!|！");
                                for (String s : split
                                        ) {
                                    int size = StringUtil.getKeySizeByLength(s);
                                    String result = getKeyWordsByHanlp(s, size);
                                    String sensitive = analysisSensitiveWords(s, sensitiveWordList);
                                    Policy_review_analysis analysis = new Policy_review_analysis();
                                    analysis.setAnalysisName(s);
                                    analysis.setReviewId(reviewId);
                                    analysis.setContentId(contentList.get(0).getContentId() + j);
                                    analysis.setAnalysisResult(result);
                                    analysis.setAnalysisSesitive(sensitive);

                                    //保存字体和是否该句包含两种及以上字体
                                    String fontName = para.getCharacterRun(0).getFontName();
                                    boolean istransform = false;
                                    //字体和是否加粗
                                    int count = 0;
                                    while (true) {
                                        CharacterRun run = para.getCharacterRun(count);// 此characterrun并非一个字符，而是一类字符，例如“数据挖掘”，前两个字为加粗，后两个字不加粗，那么“数据”　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　和“挖掘”会存在两个characterrun中
                                        if (!run.getFontName().equals(fontName)) {
                                            istransform = true;
                                            break;
                                        }
                                        //判断是不是有两种以上字体
                                        if (fontSizeType == false) {
                                            if (run.getFontSize() != range.getParagraph(0).getCharacterRun(0).getFontSize()) {
                                                fontSizeType = true;
                                            }

                                        }
                                        count++;
                                        if (run.getEndOffset() == para.getEndOffset()) {//到达段落末尾
                                            break;
                                        }
                                    }


                                    //设置信息和类型，父类句子选
                                    int type = SentenceTypeAnalysis.analysisType(s, departmentList, allPlace, lastSentence, istransform, fontName, para.getCharacterRun(0).isBold());
                                    analysis.setType(type);


                                    analysisList.add(analysis);

                                    //如果当前字体大于保存的最大字体，这时保存最大字体的内容
                                    if (null != run1 && null != runTitle && run1.getFontSize() > runTitle.getFontSize() && run1.isBold()) {
                                        runTitle = run1;
                                        policy_analysis = analysis;
                                    }
                                    //记录最大的字体，用来识别
                                    if (null == runTitle && run1.isBold()) {
                                        runTitle = run1;
                                        policy_analysis = analysis;
                                    }
                                }
                                j++;
                            }
                        }
                    } else if (url.endsWith(".docx")) {
                        XWPFDocument doc = new XWPFDocument(is);
                        List<XWPFParagraph> paras = doc.getParagraphs();
                        XWPFRun run1 = null;
                        XWPFRun run2 = null;
                        //判断待分析的是不是最后一个段落
                        int lastSentence = 0;
                        XWPFRun runTitle = null;
                        for (int i = 0; i < paras.size(); i++) {
                            XWPFParagraph paragraph = paras.get(i);
                            String paratext = paragraph.getText().trim().replaceAll("\r\n", "");
                            if (paratext.length() > 0) {
                                //处理多行标题
                                if (i > 0 && paragraph.getRuns().get(0).isBold() == true && paras.get(i - 1).getText().trim().replaceAll("\r\n", "").length() > 0) {
                                    run1 = paragraph.getRuns().get(0);
                                    run2 = paras.get(i - 1).getRuns().get(0);
                                    if (null != run1 && null != run2) {
                                        try {
                                            if (run1.getFontSize() == run2.getFontSize() && run1.isBold() == run2.isBold() && run1.isItalic() == run2.isItalic()) {
                                                analysisList.get(analysisList.size() - 1).setAnalysisName(contentList.get(j - 1).getContentText());
                                                continue;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                                wenzhai += paratext;
                                String[] split = paratext.split("。|？|\\?|!|！");
                                for (String s : split
                                        ) {
                                    int size = StringUtil.getKeySizeByLength(s);
                                    String result = getKeyWordsByHanlp(s, size);
                                    String sensitive = analysisSensitiveWords(s, sensitiveWordList);
                                    Policy_review_analysis analysis = new Policy_review_analysis();
                                    analysis.setAnalysisName(s);
                                    analysis.setReviewId(reviewId);
                                    analysis.setContentId(contentList.get(0).getContentId() + j);
                                    analysis.setAnalysisResult(result);
                                    analysis.setAnalysisSesitive(sensitive);

                                    //保存字体和是否该句包含两种及以上字体
                                    String fontFamily = paragraph.getRuns().get(0).getFontFamily();
                                    boolean istransform = false;
                                    //字体和是否加粗
                                    if (paragraph.getRuns().size() > 0) {
                                        for (XWPFRun run :
                                                paragraph.getRuns()) {
                                            if (null != run.getFontFamily() && !run.getFontFamily().equals(fontFamily)) {
                                                istransform = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (null == fontFamily) {
                                        fontFamily = "";
                                    }
                                    //设置信息和类型，父类句子选
                                    int type = SentenceTypeAnalysis.analysisType(s, departmentList, allPlace, lastSentence, istransform, fontFamily, paragraph.getRuns().get(0).isBold());
                                    analysis.setType(type);
                                    analysisList.add(analysis);
                                    for (XWPFRun run : paragraph.getRuns()
                                            ) {
                                        if (null != run) {
                                            //如果当前字体大于保存的最大字体，这时保存最大字体的内容
                                            if (null != run && null != runTitle && run.getFontSize() > runTitle.getFontSize() && run.isBold()) {
                                                runTitle = run;
                                                policy_analysis = analysis;
                                            }
                                            //记录最大的字体，用来识别
                                            if (null == runTitle && run.isBold()) {
                                                runTitle = run;
                                                policy_analysis = analysis;
                                            }
                                        }
                                    }

                                }
                                j++;
                            }
                        }
                    }
                    is.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            logger.warn("---------------核心精神提取-------------------");

            List<String> list2 = HanLP.extractPhrase(wenzhai, 3);
            for (String duanyu : list2
                    ) {
                logger.warn(duanyu);
                Policy_review_analysis p = new Policy_review_analysis();
                p.setReviewId(reviewId);
                p.setType(10);
                p.setAnalysisName(duanyu);
                analysisList.add(p);
            }
            //重置总标题的分类值
            for (Policy_review_analysis p : analysisList
                    ) {
                if (p == policy_analysis) {
                    p.setType(1);
                    break;
                }
            }

            policy_review_analysisService.insertList(analysisList);
            ifanalysis = true;
        }
        return ifanalysis;

    }

    /**
     * 根据字符串和生成关键词的个数，使用HaNLP的方式提取关键词，同时去掉关键词中的单个词
     *
     * @param s
     * @param size
     * @return
     */
    private String getKeyWordsByHanlp(String s, int size) {
        String result = "";
        List<String> strings = HanLP.extractKeyword(s, size);
        int j = 0;
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).length() > 1) {
                if (j == 0) {
                    result += strings.get(i);
                    j++;
                } else {
                    result += "," + strings.get(i);
                }
            }

        }
        return result;
    }

    /**
     * 分析出一句话中包含的敏感词
     *
     * @param s
     * @param sensitiveWordList
     * @return
     */
    private String analysisSensitiveWords(String s, List<SensitiveWord> sensitiveWordList) {
        String result = "";

        int i = 0;
        for (SensitiveWord sensitive : sensitiveWordList
                ) {
            String sensitiveName = sensitive.getSensitiveName();
            if (s.contains(sensitiveName)) {
                if (i == 0) {
                    i++;
                    result += sensitive.getSensitiveId();
                } else {
                    result += "," + sensitive.getSensitiveId();
                }

            }

        }
        return result;
    }


    /**
     * 根据id获取段落内容
     *
     * @param reviewId
     * @return
     */
    private List<Policy_review_content> getContentByReviewId(Integer reviewId) {
        /**
         * 先查询该文档是否被读取，没读取需要先读取。
         */
        logger.info("reviewId:" + reviewId);
        Integer count = policy_review_contentService.getCount(reviewId);
        logger.info("count:" + count);
        List<Policy_review_content> list = new ArrayList<>();
        if (null != count && count > 0) {
            list = policy_review_contentService.getByReviewId(reviewId);
        } else {
            Policy_review p = policy_reviewService.findById(reviewId);
            if (null != p && null != p.getUrl()) {
                solveFileByUrl(p.getUrl(), reviewId);
                list = policy_review_contentService.getByReviewId(reviewId);
                logger.info("文档分析完成");
            }
        }
        return list;
    }

    /**
     * 根据url和政策id读取政策内容，同时解决标题有换行符问题
     *
     * @param url
     * @param reviewId
     * @return
     */
    private void solveFileByUrl(String url, Integer reviewId) {
        List<Policy_review_content> list = new ArrayList<>();
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

                            Policy_review_content policy_content = new Policy_review_content();
                            policy_content.setContentText(paratext);
                            policy_content.setReviewId(reviewId);
                            policy_content.setLocation(para.getJustification());
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

                            Policy_review_content policy_content = new Policy_review_content();
                            policy_content.setContentText(paratext);
                            policy_content.setReviewId(reviewId);
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
                policy_review_contentService.insertList(list);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getAllPlace() {
        List<String> list = new ArrayList<>();
        List<Province> allProvince = placeService.getAllProvince();
        List<City> allCity = placeService.getAllCity();
        List<Area> allArea = placeService.getAllArea();
        for (Province p : allProvince
                ) {
            list.add(p.getProvinceName());
        }
        for (City c : allCity) {
            list.add(c.getCityName());
        }
        for (Area a : allArea) {
            list.add(a.getAreaName());
        }

        return list;
    }

    private void solveFileByUrl1(String url, Integer policyId) {
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
        int keySizeByLength = StringUtil.getKeySizeByLength(contentText);
        List<String> stringList = HanLP.extractPhrase(contentText, keySizeByLength);
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

class SortByDegree implements Comparator {
    public int compare(Object o1, Object o2) {
        Policy_similarity s1 = (Policy_similarity) o1;
        Policy_similarity s2 = (Policy_similarity) o2;
        //  Integer i1 = StringUtil.StringToInt(s1.getSimilarityDegree().replace("%", ""));
        //  Integer i2 = StringUtil.StringToInt(s2.getSimilarityDegree().replace("%", ""));
        Integer i1 = s1.getSimilarityDegree();
        Integer i2 = s2.getSimilarityDegree();

       /* if (i1 < i2)
            return 1;
        return -1;*/
        return i2.compareTo(i1);
    }
}
