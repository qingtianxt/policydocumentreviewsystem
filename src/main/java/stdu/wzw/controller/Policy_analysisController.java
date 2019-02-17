package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import com.hankcs.hanlp.HanLP;
import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.Ffn;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.*;
import stdu.wzw.repository.EsPolicyRepository;
import stdu.wzw.service.*;
import stdu.wzw.utils.DateIdentification;
import stdu.wzw.utils.DateUtil;
import stdu.wzw.utils.SentenceTypeAnalysis;
import stdu.wzw.utils.StringUtil;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/policyanalysis"})
public class Policy_analysisController {
    @Autowired
    private Policy_analysisService policy_analysisService;
    @Autowired
    private Policy_contentService policy_contentService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private EsPolicyRepository esPolicyRepository;

    @Autowired
    private ExecuteAndAbolishService executeAndAbolishService;

    private static Logger logger = Logger.getLogger(Policy_analysisController.class);


    /**
     * 根据政策id获取政策关键词，也就是核心精神
     *
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/getKeyWordByPolicyId")
    public List<Policy_analysis> getKeyWordByPolicyId(@RequestParam(value = "policyId") Integer policyId) {
        List<Policy_analysis> list = policy_analysisService.getByPolicyAndType(policyId, 10);
        return list;
    }

    /**
     * 根据id获取分析句子信息
     *
     * @param analysisId
     * @return
     */
    @RequestMapping(value = "/getAnalysisById")
    public Policy_analysis getAnalysisById(@RequestParam(value = "analysisId") Integer analysisId) {
        Policy_analysis p = policy_analysisService.getById(analysisId);
        return p;
    }

    /**
     * 分析出的关键词是分页展示的，核心精神是每篇文章固定的，敏感词是从分页的数据中提取的。
     *
     * @param pageCode
     * @param pageSize
     * @param policyId
     * @param msg
     * @return
     */
    @RequestMapping(value = "/getByPolicy")
    public ModelAndView getByPolicy(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, Integer policyId, String msg) {
        PageInfo<Policy_analysis> page = policy_analysisService.findSentenceByPage(pageCode, pageSize, policyId);
        List<Policy_analysis> phranceList = policy_analysisService.getByPolicyAndType(policyId, 10);
        ModelAndView mv = new ModelAndView("/analysis/analysis_info");
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        List<String> senstiveList = new ArrayList<>();
        for (Policy_analysis p : page.getList()
                ) {
            String analysisSensitive = p.getAnalysisSensitive();
            if (null != analysisSensitive && !"".equals(analysisSensitive)) {
                if (analysisSensitive.length() > 1) {
                    String[] split = analysisSensitive.split(",");
                    for (String s : split
                            ) {
                        senstiveList.add(s);
                    }
                } else {
                    senstiveList.add(analysisSensitive);
                }
            }
        }
        //新版本的 JDK
        // Stream 流操作，因此集合去重可以简单的使用下述语句来实现。
        senstiveList = senstiveList.stream().distinct().collect(Collectors.toList());
        List<SensitiveWord> sensitiveWordList = new ArrayList<>();
        for (String s : senstiveList
                ) {
            int sensitiveId = StringUtil.StringToInt(s);
            SensitiveWord sensitiveWord = sensitiveService.getById(sensitiveId);
            sensitiveWordList.add(sensitiveWord);
        }
        int totalCount = sensitiveWordList.size() + page.getList().size() + phranceList.size();
        mv.addObject("sensitiveWordList", sensitiveWordList);
        mv.addObject("page", page);
        mv.addObject("phranceList", phranceList);
        mv.addObject("policyId", policyId);
        return mv;
    }


    /**
     * 根据政策文档id分析出关键词敏感词和核心精神
     * 先按句子拆分段落，同时分词
     *
     * @param keys
     * @return
     */

    @RequestMapping(value = "/analysisContrastLibrary")
    public String analysisContrastLibrary(String keys) {
        String[] split = keys.split(",");
        for (String s : split) {
            int policy_id = StringUtil.StringToInt(s);
            analysis(policy_id);
            analysisExecuteAndAbolish(policy_id);
        }

        return "success";
    }

    /**
     * 施行日期的处理。
     *
     * @param policy_id
     */
    private void analysisExecuteAndAbolish(Integer policy_id) {
        Policy policy = policyService.findById(policy_id);
        List<Policy_analysis> effectiveList = policy_analysisService.getByPolicyAndType(policy_id, 6);
        List<ExecuteAndAbolish> executeAndAbolishList = new ArrayList<>();
        if (null != effectiveList) {
            for (Policy_analysis policy_analysis : effectiveList
                    ) {
                String ifeffective = policy_analysis.getAnalysisName();
                ExecuteAndAbolish executeAndAbolish = new ExecuteAndAbolish();

                //识别施行日期和废止日期。如果包含有效期，有效期需要转化为废止日期。

                if ((ifeffective.contains("施行") || ifeffective.contains("实行")) && ifeffective.contains("废止")) {
                    String[] splits = ifeffective.split(",|，");
                    if (splits.length > 1) {
                        for (String split : splits
                                ) {
                            setExecuteAndAbolish(split, executeAndAbolish, policy);
                        }
                    }
                } else {
                    setExecuteAndAbolish(ifeffective, executeAndAbolish, policy);
                }
                if (null != executeAndAbolish.getStartDate() || null != executeAndAbolish.getEndDate()) {
                    if (null == executeAndAbolish.getStartDate()) {
                        executeAndAbolish.setStartDate(new Date());
                    }
                    executeAndAbolish.setReference(ifeffective);
                    executeAndAbolish.setCreateDate(new Date());
                    executeAndAbolish.setPolicyId(policy_id);

                    executeAndAbolishList.add(executeAndAbolish);
                }

            }
            for (ExecuteAndAbolish executeAndAbolish : executeAndAbolishList
                    ) {
                System.out.println(executeAndAbolish);
            }
            executeAndAbolishService.insertList(executeAndAbolishList);

        }
    }

    /**
     * 设置施行日期
     */
    private void setExecuteAndAbolish(String ifeffective, ExecuteAndAbolish executeAndAbolish, Policy policy) {
        //施行日期
        if (ifeffective.contains("发布之日") || ifeffective.contains("公布之日") || ifeffective.contains("印发之日")) {
            executeAndAbolish.setStartDate(policy.getUploadDate());
        }
        if (ifeffective.endsWith("施行") || ifeffective.endsWith("实行") || ifeffective.contains("实行,") || ifeffective.contains("施行,") || ifeffective.endsWith("执行") || ifeffective.contains("执行,")) {
            try {
                String startDate = ifeffective.substring(ifeffective.indexOf("自") + 1, ifeffective.indexOf("日") + 1);
                if (DateIdentification.ifWordDate(startDate)) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");

                    executeAndAbolish.setStartDate(format.parse(startDate));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //废止日期
        if (ifeffective.contains("同时")) {
            executeAndAbolish.setEndDate(policy.getUploadDate());
        }
        if (ifeffective.contains("废止")) {
            try {
                String endDate = ifeffective.substring(ifeffective.indexOf("自") + 1, ifeffective.indexOf("日") + 1);
                if (DateIdentification.ifWordDate(endDate)) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                    executeAndAbolish.setEndDate(format.parse(endDate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ifeffective.contains("至") && ifeffective.contains("日")) {
            try {
                int startIndex = ifeffective.indexOf("至") + 1;
                int endIndex = 0;
                for (int i = 0; i < ifeffective.length(); i++) {
                    if ((ifeffective.charAt(i) + "").equals("日")) {
                        if (startIndex < i) {
                            endIndex = i + 1;
                            break;
                        }
                    }

                }
                String endDate = ifeffective.substring(startIndex, endIndex);
                if (DateIdentification.ifWordDate(endDate)) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                    executeAndAbolish.setEndDate(format.parse(endDate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ifeffective.contains("本办法"))

        {
            executeAndAbolish.setExecuteName("本办法");
        }
        if (ifeffective.contains("本细则"))

        {
            executeAndAbolish.setExecuteName("本细则");
        }
        if (ifeffective.contains("本通知"))

        {
            executeAndAbolish.setExecuteName("本通知");
        }
        if (ifeffective.contains("《") || ifeffective.contains("》"))

        {
            if (ifeffective.contains("《") || ifeffective.contains("》")) {
                String abolishName = ifeffective.substring(ifeffective.indexOf("《"), ifeffective.indexOf("》") + 1);
                executeAndAbolish.setAbolishName(abolishName);
            } else if (ifeffective.contains("》")) {
                String abolishName = ifeffective.substring(0, ifeffective.indexOf("》") + 1);
                executeAndAbolish.setAbolishName(abolishName);
            }
        }

    }

    /**
     * 列表中如果有未分析的手动分析，并展示分析结果
     *
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/analysisById")
    public ModelAndView analysisById(@RequestParam(value = "policyId") Integer policyId) {
        analysis(policyId);
        ModelAndView mv = new ModelAndView("redirect:/policyanalysis/getByPolicy?policyId=" + policyId);
        return mv;
    }


    /**
     * 分析关键词,并返回该文件是否被分析
     *
     * @param policyId
     * @return
     */
    private boolean analysis(Integer policyId) {

        boolean ifanalysis = false;
        Integer countAnalysis = policy_analysisService.getCountByPolicyId(policyId);
        logger.info("countAnalysis:" + countAnalysis);

        if (countAnalysis == 0) {
            List<Policy_analysis> analysisList = new ArrayList<>();
            //最先要判断的是该文档有没有被分析，如果已经被分析则直接获取分析结果展示在页面上

            Policy policy = policyService.findById(policyId);
            //先判断文档有没有分段，没有分段的文档需要先分段
            Integer contentCount = policy_contentService.getCount(policyId);
            //count等于0.先分段
            if (contentCount == 0) {
                solveFileByUrl(policy.getUrl(), policyId);
            }
            String content_text = "";
            //分段，分析关键词等内容，包含在段落分解中(因为要分析句子的格式，必须重新读取文件分段)
            //获取段落内容，查看标题是否是两句话合并而来
            List<Policy_content> contentList = policy_contentService.getById(policyId);
            //获取敏感词
            List<SensitiveWord> sensitiveWordList = sensitiveService.findAll();
            //获取部门
            List<Department> departmentList = departmentService.getAll();
            //获取地址
            List<String> allPlace = getAllPlace();
            //记录总标题的内容
            Policy_analysis policy_analysis = new Policy_analysis();
            //判断有几种字体，用来识别是不是总标题
            boolean fontSizeType = false;


            String wenzhai = "";
            String url = policy.getUrl();

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
                                    Policy_analysis analysis = new Policy_analysis();
                                    analysis.setAnalysisName(s);
                                    analysis.setCreateDate(new Date());
                                    analysis.setPolicyId(policyId);
                                    analysis.setAnalysisResult(result);
                                    analysis.setAnalysisSensitive(sensitive);
                                    analysis.setContentId(contentList.get(0).getContentId() + j);

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
                                    Policy_analysis analysis = new Policy_analysis();
                                    analysis.setAnalysisName(s);
                                    analysis.setCreateDate(new Date());
                                    analysis.setPolicyId(policyId);
                                    analysis.setAnalysisResult(result);
                                    analysis.setAnalysisSensitive(sensitive);
                                    analysis.setContentId(contentList.get(0).getContentId() + j);

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
                Policy_analysis p = new Policy_analysis();
                p.setPolicyId(policyId);
                p.setCreateDate(new Date());
                p.setType(10);
                p.setAnalysisName(duanyu);
                analysisList.add(p);
            }
            //重置总标题的分类值
            for (Policy_analysis p : analysisList
                    ) {
                if (p == policy_analysis) {
                    p.setType(1);
                    break;
                }
            }

            policy_analysisService.insertList(analysisList);
            policyService.updateStatus(policyId, 1);
            ifanalysis = true;
        }
        return ifanalysis;

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
     * 获取所有地址信息
     *
     * @return
     */
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

}
