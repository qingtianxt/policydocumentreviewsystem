package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import com.hankcs.hanlp.HanLP;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
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
import stdu.wzw.condition.PolicyCondition;
import stdu.wzw.model.*;
import stdu.wzw.service.*;
import stdu.wzw.utils.WordUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * 对比模块，主要用于处理文档对比内容
 */
@RestController
@RequestMapping(value = {"/policycompare"})
public class Policy_compareController {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Policy_reviewController.class);

    @Autowired
    private Policy_contentService policy_contentService;
    @Autowired
    private Policy_review_contentService policy_review_contentService;

    @Autowired
    private Policy_reviewService policy_reviewService;

    @Autowired
    private Policy_review_analysisService policy_review_analysisService;

    @Autowired
    private Policy_similarityService policy_similarityService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private Policy_analysisService policy_analysisService;
    @Autowired
    private Policy_compare_ULrelationshipService uLrelationshipService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PlaceService placeService;

    /**
     * 展示执行效果
     *
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/showImplementationsituation")
    public ModelAndView showImplementationsituation(Integer policyId, String relatedPolicyId) {

        return new ModelAndView("");
    }


    @RequestMapping(value = "/getReDepartment")
    public List<Department> getReDepartment(Integer reviewId) {
        List<Policy_compare_ULrelationship> lrelationshipList = uLrelationshipService.getByReviewId(reviewId);
        List<Department> departmentList = new ArrayList<>();//这里department只是一个容器，departmentName作为城市的名字，provinceId作为provinceId，grade作为数量
        for (Policy_compare_ULrelationship p : lrelationshipList
                ) {
            Policy policy = policyService.findById(p.getPolicyId());
            Department department = departmentService.getByDepartmentId(userService.getById(policy.getUserId()).getDepartmentId());
            if (departmentList.size() == 0) {
                Department department1 = new Department();
                String provinceId = department.getProvinceId();
                Province province = placeService.getByProvinceId(provinceId);

                department1.setDepartmentName(province.getProvinceName());
                department1.setProvinceId(province.getProvinceId());
                department1.setGrade(1);
                departmentList.add(department1);
            } else {
                int i = 0;
                for (Department d : departmentList
                        ) {
                    if (d.getProvinceId().equals(department.getProvinceId())) {
                        d.setGrade(d.getGrade() + 1);
                        i++;
                        break;
                    }
                }
                if (i == 0) {
                    Department department1 = new Department();
                    String provinceId = department.getProvinceId();
                    Province province = placeService.getByProvinceId(provinceId);

                    department1.setDepartmentName(province.getProvinceName());
                    department1.setProvinceId(province.getProvinceId());
                    department1.setGrade(1);
                    departmentList.add(department1);
                }
            }
        }
        return departmentList;
    }

    @RequestMapping(value = "/downloadReport")
    public ModelAndView downloadReport(String url, Integer reviewid, HttpServletRequest request, HttpServletResponse resp) {
        String fileName = url;
        String filePath = "F:\\review\\compare\\report\\" + url;

        //复制相似的文档到产生的目录下，然后准备打包
        Policy_review policy_review = policy_reviewService.findById(reviewid);
        Integer grade = policy_review.getGrade();
        if (grade > 1 && grade <= 4) {
            List<Policy_compare_ULrelationship> ulist = uLrelationshipService.getByReviewIdAndGrade(reviewid, grade - 1);
            for (Policy_compare_ULrelationship uLrelationship : ulist
                    ) {
                Policy policy = policyService.findById(uLrelationship.getPolicyId());
                String url1 = policy.getUrl();
            }
        }


        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(filePath);
            bis = new BufferedInputStream(fis);

            resp.reset();
            resp.setContentType("application/force-download");// 设置强制下载不打开
            resp.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            OutputStream outputStream = resp.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("下载完成");
        ModelAndView mv = new ModelAndView("redirect:/policycompare/list?msg=2");
        return mv;
    }

    @RequestMapping(value = "/makeWordReport")
    public ModelAndView makeWordReport(@RequestParam(value = "reviewid") Integer reviewId, HttpServletRequest request, HttpServletResponse resp) {

        Policy_review policy_review = policy_reviewService.findById(reviewId);

        Map<String, Object> map = getReportMap(reviewId);
        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        StringBuffer sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));

        //文件唯一路径
        String filePath = "F:\\review\\compare\\report\\" + sb;
        //文件名称
        String fileName = policy_review.getReviewName().replaceAll(".docx", "").replaceAll(".doc", "").replaceAll(" ", "") + "_对比报告.doc";
        WordUtil.createWord(map, "finalCompare.ftl", filePath, fileName);
        System.out.println("---------------文档制作完成----------");
        //文档下载

        String filePath1 = filePath + "\\" + fileName;
        //复制相似的文档到产生的目录下，然后准备打包
        System.out.println();
        Integer grade = policy_review.getGrade();
        if (grade > 1 && grade <= 4) {
            List<Policy_compare_ULrelationship> ulist = uLrelationshipService.getByReviewIdAndGrade(reviewId, grade - 1);
            for (Policy_compare_ULrelationship uLrelationship : ulist
                    ) {
                Policy policy = policyService.findById(uLrelationship.getPolicyId());
                String url1 = policy.getUrl();
                String finalUrl = filePath + "\\上级相关_" + policy.getPolicyName().replaceAll(" ", "");
                File inFile = new File(url1);
                File outFile = new File(finalUrl);
                try {
                    FileUtils.copyFile(inFile, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (grade >= 1 && grade <= 4) {
            List<Policy_compare_ULrelationship> ulist = uLrelationshipService.getByReviewIdAndGrade(reviewId, grade);
            for (Policy_compare_ULrelationship uLrelationship : ulist
                    ) {
                Policy policy = policyService.findById(uLrelationship.getPolicyId());
                String url1 = policy.getUrl();
                String finalUrl = filePath + "\\同级相关_" + policy.getPolicyName().replaceAll(" ", "");
                File inFile = new File(url1);
                File outFile = new File(finalUrl);
                try {
                    FileUtils.copyFile(inFile, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (grade >= 1 && grade < 4) {
            List<Policy_compare_ULrelationship> ulist = uLrelationshipService.getByReviewIdAndGrade(reviewId, grade + 1);
            for (Policy_compare_ULrelationship uLrelationship : ulist
                    ) {
                Policy policy = policyService.findById(uLrelationship.getPolicyId());
                String url1 = policy.getUrl();
                String finalUrl = filePath + "\\下级相关_" + policy.getPolicyName().replaceAll(" ", "");
                File inFile = new File(url1);
                File outFile = new File(finalUrl);
                try {
                    FileUtils.copyFile(inFile, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<Policy_similarity> similarityList = policy_similarityService.getPolicySimilarity(reviewId);
        for (Policy_similarity similarity : similarityList
                ) {
            Policy policy = policyService.findById(similarity.getSimilarityPolicyId());
            String url1 = policy.getUrl();
            String finalUrl = filePath + "\\内容相关_" + policy.getPolicyName().replaceAll(" ", "");
            System.out.println("finalUrl:" + finalUrl);
            File inFile = new File(url1);
            File outFile = new File(finalUrl);
            try {
                FileUtils.copyFile(inFile, outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stdu.wzw.utils.FileUtils.fileToZip(filePath, "F:\\review\\compare\\report\\", "" + sb);

        String zipPath = filePath + ".zip";
        String zipName = sb + ".zip";

        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(zipPath);
            bis = new BufferedInputStream(fis);

            resp.reset();
            resp.setContentType("application/force-download");// 设置强制下载不打开
            resp.addHeader("Content-Disposition", "attachment;fileName=" + new String(zipName.getBytes("gb2312"), "ISO8859-1"));
            OutputStream outputStream = resp.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("下载完成");

        ModelAndView mv = new ModelAndView("redirect:/policycompare/list?msg=2");
        return mv;
    }

    /**
     * 获取要传到ftl的map
     *
     * @param reviewId
     * @return
     */
    private Map<String, Object> getReportMap(Integer reviewId) {
        Map<String, Object> map = new HashMap<>();
        Policy_review policy_review = policy_reviewService.findById(reviewId);
        List<Policy_compare_ULrelationship> urelationshipList = new ArrayList<>();//上级相关
        List<Policy_compare_ULrelationship> srelationshipList = new ArrayList<>();//同级相关
        List<Policy_compare_ULrelationship> lrelationshipList = new ArrayList<>();//下级相关

        List<CompareForWord> ucompareForWordList = new ArrayList<>();
        List<CompareForWord> scompareForWordList = new ArrayList<>();
        List<CompareForWord> lcompareForWordList = new ArrayList<>();
        List<CompareForWord> ccompareForWordList = new ArrayList<>();//内容相关

        List<CompareForWordContent> list = new ArrayList<>();

        //获取上下级信息
        int review_grade = policy_review.getGrade();
        if (review_grade > 1 && review_grade <= 4) {
            urelationshipList = uLrelationshipService.getByReviewIdAndGrade(reviewId, review_grade - 1);
        }
        if (review_grade >= 1 && review_grade <= 4) {
            srelationshipList = uLrelationshipService.getByReviewIdAndGrade(reviewId, review_grade);
        }
        if (review_grade >= 1 && review_grade < 4) {
            lrelationshipList = uLrelationshipService.getByReviewIdAndGrade(reviewId, review_grade + 1);
        }
        List<Policy_similarity> similarityList = policy_similarityService.getPolicySimilarity(reviewId);

        if (urelationshipList.size() > 0) {
            for (Policy_compare_ULrelationship compare : urelationshipList
                    ) {
                Policy policy = policyService.findById(compare.getPolicyId());
                User user = userService.getById(policy.getUserId());
                Department department = departmentService.getByDepartmentId(user.getDepartmentId());
                String ifAbolish = "否";
                CompareForWord compareForWord = new CompareForWord(policy.getUploadDate().toString(), policy.getPolicyName(), user.getUsername(), department.getDepartmentName(), ifAbolish);
                ucompareForWordList.add(compareForWord);
            }
            map.put("u", ucompareForWordList);
        }
        if (srelationshipList.size() > 0) {
            for (Policy_compare_ULrelationship compare : srelationshipList
                    ) {
                Policy policy = policyService.findById(compare.getPolicyId());
                User user = userService.getById(policy.getUserId());
                Department department = departmentService.getByDepartmentId(user.getDepartmentId());
                String ifAbolish = "否";
                CompareForWord compareForWord = new CompareForWord(policy.getUploadDate().toString(), policy.getPolicyName(), user.getUsername(), department.getDepartmentName(), ifAbolish);
                scompareForWordList.add(compareForWord);
            }
            map.put("s", scompareForWordList);
        }
        if (lrelationshipList.size() > 0) {
            for (Policy_compare_ULrelationship compare : lrelationshipList
                    ) {
                Policy policy = policyService.findById(compare.getPolicyId());
                User user = userService.getById(policy.getUserId());
                Department department = departmentService.getByDepartmentId(user.getDepartmentId());
                String ifAbolish = "否";
                CompareForWord compareForWord = new CompareForWord(policy.getUploadDate().toString(), policy.getPolicyName(), user.getUsername(), department.getDepartmentName(), ifAbolish);
                lcompareForWordList.add(compareForWord);
            }
            map.put("l", lcompareForWordList);
        }
        if (similarityList.size() > 0) {
            for (Policy_similarity similarity : similarityList
                    ) {
                Policy policy = policyService.findById(similarity.getSimilarityPolicyId());
                User user = userService.getById(policy.getUserId());
                Department department = departmentService.getByDepartmentId(user.getDepartmentId());
                String ifAbolish = "否";
                CompareForWord compareForWord = new CompareForWord(policy.getUploadDate().toString(), policy.getPolicyName(), user.getUsername(), department.getDepartmentName(), ifAbolish);
                ccompareForWordList.add(compareForWord);
            }
            map.put("c", ccompareForWordList);
        }
        List<Policy_review_analysis> analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
        //获取段落信息，并分析段落摘要
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
        int cycleTimes = (int) Math.ceil(contentList.size() / 5) + 1;
        int maxSimilarity = 0;
        for (int i = 0; i < cycleTimes; i++) {
            int k = 0;
            List<Policy_review_analysis> analysisList2 = new ArrayList<>();
            List<CompareForWord> compareForWordList = new ArrayList<>();
            String content = "";
            int totalSimilarity = 0;
            while (k < 5 && (i * 5 + k < contentList.size())) {
                int index = i * 5 + k;
                List<Policy_review_analysis> analysisList1 = new ArrayList<>();
                //查找到每个段落所包含的句子
                for (Policy_review_analysis analysis : analysisList
                        ) {
                    if (analysis.getContentId().intValue() == contentList.get(index).getContentId().intValue()) {
                        analysisList1.add(analysis);
                    }
                }
                //对每个句子获取相似信息
                for (Policy_review_analysis analysis : analysisList
                        ) {
                    List<Policy_similarity> analysisSimilarityList = policy_similarityService.getAnalysisSimilarity(analysis.getAnalysisId());
                    for (Policy_similarity similarity : analysisSimilarityList
                            ) {
                        if (similarity.getSimilarityDegree() >= 30) {
                            Policy policy = policyService.findById(similarity.getSimilarityPolicyId());
                            int index1 = -1;//保存相同的内容的下标
                            int j = 0;
                            for (int i1 = 0; i1 < compareForWordList.size(); i1++) {
                                if (compareForWordList.get(i1).getPolicyName().equals(policy.getPolicyName())) {
                                    if (compareForWordList.get(i1).getMaxSimilarity() < similarity.getSimilarityDegree()) {
                                        index1 = i1;
                                        break;
                                    }
                                    j++;
                                }
                            }
                            //j等于0，说明没有相同的内容，i1下标大于-1，d，需要重置这个最大的相似度
                            if (index1 >= 0) {
                                compareForWordList.get(index1).setMaxSimilarity(similarity.getSimilarityDegree());
                            }
                            if (j == 0) {
                                User user = userService.getById(policy.getUserId());
                                Department department = departmentService.getByDepartmentId(user.getDepartmentId());
                                CompareForWord compareForWord = new CompareForWord(policy.getUploadDate().toString(), policy.getPolicyName(), user.getUsername(), department.getDepartmentName(), similarity.getSimilarityDegree(), "否");
                                compareForWordList.add(compareForWord);
                            }

                        }
                    }
                }
                content += contentList.get(index).getContentText();
                analysisList2.addAll(analysisList1);
                k++;
            }
            if (compareForWordList.size() > 0) {
                Collections.sort(compareForWordList, new SortBySimilarityDegree());
                totalSimilarity = compareForWordList.get(0).getMaxSimilarity();
                if (compareForWordList.size() > 15) {
                    compareForWordList = compareForWordList.subList(0, 20);
                }
            }

            if (totalSimilarity > maxSimilarity) {
                maxSimilarity = totalSimilarity;
            }

            String contentSummary = HanLP.extractPhrase(content, 1).get(0);
            CompareForWordContent compareForWordContent = new CompareForWordContent(compareForWordList, totalSimilarity, contentSummary, analysisList2);
            list.add(compareForWordContent);
        }
        map.put("l2", list);
        //遍历完成，将最大相似度塞到里面

        User user = userService.getById(policy_review.getUserId());
        Department department = departmentService.getByDepartmentId(user.getDepartmentId());

        CompareForWord compareForWord = new CompareForWord(policy_review.getReviewid(), policy_review.getUploadDate().toString(), policy_review.getReviewName(), user.getUsername(), department.getDepartmentName(), maxSimilarity);
        map.put("p", compareForWord);
        return map;
    }


    /**
     * 获取上下级相关信息
     *
     * @param reviewId
     * @return
     */
    @RequestMapping(value = "/getanalysisUL")
    public List<Policy_compare_ULrelationship> getanalysisUL(@RequestParam(value = "reviewId") Integer reviewId, @RequestParam(value = "grade") Integer grade) {
        return uLrelationshipService.getByReviewIdAndGrade(reviewId, grade);
    }

    //通过审查政策id获取整体信息
    @RequestMapping(value = "/getReport")
    public ModelAndView getReport(@RequestParam(value = "reviewid") Integer reviewId) {
        ModelAndView mv = new ModelAndView("/review/article-report");
        logger.info(reviewId);
        Policy_review policy_review = policy_reviewService.findById(reviewId);
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_review_analysis> list = policy_review_analysisService.getBasedByReviewId(reviewId);
        mv.addObject("policy_review", policy_review);
        mv.addObject("contentList", contentList);
        mv.addObject("list", list);
        mv.addObject("reviewid", reviewId);
        return mv;
    }

    /**
     * 展示对比列表页面
     *
     * @param pageCode
     * @param pageSize
     * @param policyCondition
     * @param msg
     * @return
     */
    @RequestMapping(value = "/list")
    public ModelAndView compareList(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, PolicyCondition policyCondition, String msg) {
        PageInfo<Policy_review> list = policy_reviewService.findByPageAndType(pageCode, pageSize, 1);
        ModelAndView mv = new ModelAndView("/review/review_compare", "page", list);
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }


    /**
     * 获取内容相关的相似信息
     *
     * @param reviewId
     * @return
     */
    @RequestMapping(value = "/getTextSimilarity")
    public List<Policy_similarity> getTextSimilarity(@RequestParam(value = "reviewId") Integer reviewId) {
        return policy_similarityService.getPolicySimilarity(reviewId);
    }

    /**
     * 根据审查内容id和对比文档的id获取相似信息
     *
     * @param reviewAnalysisId
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/getAnalysisSimilarityByPolicyId")
    public List<Policy_similarity> getAnalysisSimilarityByPolicyId(@RequestParam(value = "reviewAnalysisId") Integer reviewAnalysisId, @RequestParam(value = "policyId") Integer policyId) {
        return policy_similarityService.getAnalysisSimilarityByPolicyId(reviewAnalysisId, policyId);
    }

    /**
     * 找到两个文档的analysis，并找到对比句子和另一篇文档的最大相似信息，并将左边句子的相似度设置为该值。
     *
     * @param reviewId
     * @param policyId
     * @param analysisId
     * @return
     */
    @RequestMapping(value = "/getAnalysisDetail")
    public ModelAndView getAnalysisDetail(@RequestParam(value = "reviewid") Integer reviewId, @RequestParam(value = "policyId") Integer policyId, @RequestParam(value = "analysisId") Integer analysisId) {

        List<Policy_review_content> policy_review_contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_content> policy_contents = policy_contentService.getById(policyId);
        List<Policy_review_analysis> review_analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
        List<Policy_analysis> analysisList = policy_analysisService.getByPolicyBased(policyId);
        for (Policy_review_analysis p : review_analysisList
                ) {
            List<Policy_similarity> list = policy_similarityService.getAnalysisSimilarityByPolicyId(p.getAnalysisId(), policyId);
            int maxSimilarity = 0;
            for (Policy_similarity policy_similarity : list
                    ) {
                if (maxSimilarity < policy_similarity.getSimilarityDegree()) {
                    maxSimilarity = policy_similarity.getSimilarityDegree();
                }
            }
            p.setSimilarityDegree(maxSimilarity);
        }

        ModelAndView mv = new ModelAndView("/compare/compare_analysis_detail");

        mv.addObject("policy_contents", policy_contents);
        mv.addObject("policy_review_contentList", policy_review_contentList);
        mv.addObject("analysisList", analysisList);
        mv.addObject("review_analysisList", review_analysisList);
        mv.addObject("reviewAnalysisId", analysisId);
        return mv;
    }


    /**
     * 根据审查内容id获取相似信息
     *
     * @param analysisId
     * @return
     */
    @RequestMapping(value = "/getAnalysisSimilarity")
    public List<Policy_similarity> getAnalysisSimilarity(@RequestParam(value = "analysisId") Integer analysisId) {
        return policy_similarityService.getAnalysisSimilarity(analysisId);
    }

    /**
     * 根据审查内容id和对比文档的id获取相似信息
     *
     * @param reviewContentId
     * @param policyId
     * @return
     */
    @RequestMapping(value = "/getContentSimilarityByPolicyId")
    public List<Policy_similarity> getContentSimilarityByPolicyId(@RequestParam(value = "reviewContentId") Integer reviewContentId, @RequestParam(value = "policyId") Integer policyId) {
        return policy_similarityService.getContentSimilarityByPolicyId(reviewContentId, policyId);
    }

    /**
     * 找到两个文档的content，并找到对比段落和另一篇文档的最大相似信息，并将左边段落的相似度设置为该值。
     *
     * @param reviewId
     * @param policyId
     * @param contentId
     * @return
     */
    @RequestMapping(value = "/getContentDetail")
    public ModelAndView getContentDetail(@RequestParam(value = "reviewid") Integer reviewId, @RequestParam(value = "policyId") Integer policyId, @RequestParam(value = "contentId") Integer contentId) {

        List<Policy_review_content> policy_review_contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_content> policy_contents = policy_contentService.getById(policyId);
        for (Policy_review_content p : policy_review_contentList
                ) {
            List<Policy_similarity> list = policy_similarityService.getContentSimilarityByPolicyId(p.getContentId(), policyId);
            int maxSimilarity = 0;
            for (Policy_similarity policy_similarity : list
                    ) {
                if (maxSimilarity < policy_similarity.getSimilarityDegree()) {
                    maxSimilarity = policy_similarity.getSimilarityDegree();
                }
            }
            p.setSimilarityDegree(maxSimilarity);
        }

        ModelAndView mv = new ModelAndView("/compare/compare_content_detail");
        mv.addObject("policy_contents", policy_contents);
        mv.addObject("contentId", contentId);
        mv.addObject("policy_review_contentList", policy_review_contentList);

        return mv;
    }

    /**
     * 根据审查内容id获取相似信息
     *
     * @param contentId
     * @return
     */
    @RequestMapping(value = "/getContentSimilarity")
    public List<Policy_similarity> getContentSimilarity(@RequestParam(value = "contentId") Integer contentId) {
        return policy_similarityService.getContentSimilarity(contentId);
    }

    /**
     * 判断对比分析的手段,并展示文档的内容和对比情况
     *
     * @param reviewId
     * @param type
     * @return
     */
    @RequestMapping(value = "/getContentByReviewidAndType")
    public ModelAndView getContentByReviewidAndType(@RequestParam(value = "reviewid") Integer reviewId, @RequestParam(value = "type") Integer type) {


        ModelAndView mv = new ModelAndView();
        if (type == 1) {
            List<Policy_review_content> list = getContentByReviewId(reviewId);
            Policy_review policy_review = policy_reviewService.findById(reviewId);
            mv.setViewName("/compare/compare_fulltext");
            mv.addObject("contentList", list);
            mv.addObject("policy_review", policy_review);
        } else if (type == 2) {
            List<Policy_review_content> list = getContentByReviewId(reviewId);
            mv.setViewName("/compare/compare_paragraph");
            mv.addObject("contentList", list);
        } else if (type == 3) {
            List<Policy_review_content> list = getContentByReviewId(reviewId);
            List<Policy_review_analysis> analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
            mv.addObject("analysisList", analysisList);
            mv.addObject("contentList", list);
            mv.setViewName("/compare/compare_sentence");
        }

        return mv;
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
     * 根据url和政策id读取政策内容，同时解决标题有换行符问题,存储的是审查政策
     *
     * @param url
     * @param policyId
     * @return
     */
    private void solveFileByUrl(String url, Integer policyId) {
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

                            Policy_review_content policy_content = new Policy_review_content();
                            policy_content.setContentText(paratext);
                            policy_content.setReviewId(policyId);
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
                            policy_content.setReviewId(policyId);
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


}


class SortBySimilarityDegree implements Comparator {
    public int compare(Object o1, Object o2) {
        CompareForWord c1 = (CompareForWord) o1;
        CompareForWord c2 = (CompareForWord) o2;
        //  Integer i1 = StringUtil.StringToInt(s1.getSimilarityDegree().replace("%", ""));
        //  Integer i2 = StringUtil.StringToInt(s2.getSimilarityDegree().replace("%", ""));
        Integer i1 = c1.getMaxSimilarity();
        Integer i2 = c2.getMaxSimilarity();

       /* if (i1 < i2)
            return 1;
        return -1;*/
        return i2.compareTo(i1);
    }
}