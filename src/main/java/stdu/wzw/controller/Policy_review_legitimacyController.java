package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import com.hankcs.hanlp.HanLP;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.*;
import stdu.wzw.service.LegitimacyanalysisService;
import stdu.wzw.service.Policy_reviewService;
import stdu.wzw.service.Policy_review_analysisService;
import stdu.wzw.service.Policy_review_contentService;
import stdu.wzw.utils.WordUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = {"/reviewlegitimacy"})
public class Policy_review_legitimacyController {

    @Autowired
    private Policy_reviewService policy_reviewService;

    @Autowired
    private Policy_review_analysisService policy_review_analysisService;

    @Autowired
    private Policy_review_contentService policy_review_contentService;

    @Autowired
    private LegitimacyanalysisService legitimacyanalysisService;


    @RequestMapping(value = "/downloadReport")
    public ModelAndView downloadReport(String url, HttpServletRequest request, HttpServletResponse resp) {

        String fileName = url;
        String filePath = "F:\\review\\legitimacyAnalysis\\report\\" + url;



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
        ModelAndView mv = new ModelAndView("redirect:/reviewlegitimacy/list");
        return mv;
    }

    @RequestMapping(value = "/makeWordReport")
    public ModelAndView makeWordReport(@RequestParam(value = "reviewid") Integer reviewId) {

        Map<String, Object> map = getReportMap(reviewId);
        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        StringBuffer sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));

        //文件路径
        String filePath = "F:\\review\\legitimacyAnalysis\\report";
        //文件唯一名称
        String fileOnlyName = "word" + sb + ".doc";

        WordUtil.createWord(map, "6.ftl", filePath, fileOnlyName);
        ModelAndView mv = new ModelAndView("redirect:/reviewlegitimacy/downloadReport?url=" + fileOnlyName);
        System.out.println("---------------文档制作完成----------");
        return mv;
    }


    @RequestMapping(value = "/getAnalysislegitimacy")
    public List<Legitimacy_analysis> getAnalysislegitimacy(@RequestParam(value = "analysisId") Integer analysisId) {

        return legitimacyanalysisService.getByAnalysisId(analysisId);
    }


    @RequestMapping(value = "/list")
    public ModelAndView list(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, String msg) {
        PageInfo<Policy_review> list = policy_reviewService.findByPageAndType(pageCode, pageSize, 2);
        ModelAndView mv = new ModelAndView("/legitimacyAnalysis/analysis_list", "page", list);
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }

    @RequestMapping(value = "/getReport")
    public ModelAndView getReport(@RequestParam(value = "reviewid") Integer reviewId) {


        ModelAndView mv = new ModelAndView();
        List<Policy_review_content> list = getContentByReviewId(reviewId);
        List<Policy_review_analysis> analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
        mv.addObject("analysisList", analysisList);
        mv.addObject("contentList", list);
        mv.setViewName("/legitimacyAnalysis/analysis_report");


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
        Integer count = policy_review_contentService.getCount(reviewId);
        List<Policy_review_content> list = new ArrayList<>();
        if (null != count && count > 0) {
            list = policy_review_contentService.getByReviewId(reviewId);
        } else {
            Policy_review p = policy_reviewService.findById(reviewId);
            if (null != p && null != p.getUrl()) {
                solveFileByUrl(p.getUrl(), reviewId);
                list = policy_review_contentService.getByReviewId(reviewId);
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


    /**
     * 制作word文档
     *
     * @param reviewId
     */
    private Map<String, Object> getReportMap(Integer reviewId) {

        Map<String, Object> map = new HashMap<>();

        Policy_review policy_review = policy_reviewService.findById(reviewId);
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_review_analysis> analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
        List<Legitimacy_analysis> legitimacy_analysisList = legitimacyanalysisService.getAllByReviewId(reviewId);


        int quanwenDegree = 0;
        List<Legitimacy_analysis> wrongablelist = new ArrayList<>();//不合法信息
        List<Legitimacy_analysis> unreasonablelist = new ArrayList<>();//不合理信息
        List<Integer> wrongableTypeList = new ArrayList<>();
        List<Integer> unreasonableTypelist = new ArrayList<>();


        //存储全文不合理和不合法信息
        for (Legitimacy_analysis legitimacy_analysis : legitimacy_analysisList
                ) {

            //如果type为8时，说明是全文审查信息
            if (legitimacy_analysis.getType() == 8) {
                quanwenDegree = legitimacy_analysis.getLegitimacy();
            } else {
                if (legitimacy_analysis.getLegitimacy() == 30) {
                    wrongablelist.add(legitimacy_analysis);
                    int i = 0;
                    for (Integer wronge : wrongableTypeList
                            ) {
                        if (legitimacy_analysis.getType() == wronge) {
                            i++;
                            break;
                        }
                    }
                    if (i == 0) {
                        wrongableTypeList.add(legitimacy_analysis.getType());
                    }


                } else if (legitimacy_analysis.getLegitimacy() == 50) {
                    unreasonablelist.add(legitimacy_analysis);
                    int i = 0;
                    for (Integer unreasonable : unreasonableTypelist
                            ) {
                        if (legitimacy_analysis.getType() == unreasonable) {
                            i++;
                            break;
                        }
                    }
                    if (i == 0) {
                        unreasonableTypelist.add(legitimacy_analysis.getType());
                    }
                }
            }
        }

        LegitimacyForWord legitimacyForWord = new LegitimacyForWord(policy_review.getReviewid(), policy_review.getUploadDate().toString(), policy_review.getReviewName(), "张三", quanwenDegree, contentList.size(), analysisList.size(), wrongablelist.size(), wrongableTypeList.size(), unreasonablelist.size(), unreasonableTypelist.size());
        map.put("w", legitimacyForWord);

        //每5个段落算作一部分

        int cycleTimes = (int) Math.ceil(contentList.size() / 5) + 1;

        System.out.println("一共" + contentList.size() + "段，循环" + cycleTimes + "次");
        List<LegitimacyForWordContent> forWordContentList = new ArrayList<>();
        for (int i = 0; i < cycleTimes; i++) {
            System.out.println("----------------开始循环第：" + (i + 1) + "次--------------");
            int degree = 100;//记录评分的最小值
            String paragraphContent = "";
            List<Legitimacy_analysis> legitimacy_analysisList1 = new ArrayList<>();//不合法信息
            List<Legitimacy_analysis> legitimacy_analysisList2 = new ArrayList<>();//不合理信息
            List<String> reviewInfo = new ArrayList<>();
            List<Integer> reviewType = new ArrayList<>();
            List<String> reviewbasis = new ArrayList<>();
            List<Legitimacy_analysis> legitimacy_analysisList3 = new ArrayList<>();

            List<Policy_review_analysis> analysisList1 = new ArrayList<>();//跟循环的content相关的句子
            int k = 0;
            while (k < 5 && (i * 5 + k < contentList.size())) {
                int index = i * 5 + k;
                for (Policy_review_analysis analysis : analysisList
                        ) {
                    if (analysis.getContentId().intValue() == contentList.get(index).getContentId().intValue()) {
                        analysisList1.add(analysis);
                    }
                }
                paragraphContent += contentList.get(index).getContentText();


                for (Policy_review_analysis analysis : analysisList1
                        ) {

                    for (Legitimacy_analysis legitimacy_analysis : legitimacy_analysisList
                            ) {

                        if (legitimacy_analysis.getAnalysisId() != null && (analysis.getAnalysisId().intValue() == legitimacy_analysis.getAnalysisId().intValue())) {
                            if (legitimacy_analysis.getLegitimacy() == 30) {
                                legitimacy_analysisList1.add(legitimacy_analysis);
                            } else if (legitimacy_analysis.getLegitimacy() == 50) {
                                legitimacy_analysisList2.add(legitimacy_analysis);
                            }
                            if (degree >= legitimacy_analysis.getLegitimacy()) {
                                degree = legitimacy_analysis.getLegitimacy();
                            }
                        }
                    }
                }
                k++;
            }
            String contentSummary = HanLP.extractPhrase(paragraphContent, 1).get(0);

            legitimacy_analysisList3.addAll(legitimacy_analysisList1);
            legitimacy_analysisList3.addAll(legitimacy_analysisList2);
            for (Legitimacy_analysis legitimacy_analysis : legitimacy_analysisList3
                    ) {
                int i1 = 0;
                for (String s : reviewInfo
                        ) {
                    if (s.equals(legitimacy_analysis.getInfo())) {
                        i1++;
                        break;
                    }
                }
                if (i1 == 0) {
                    reviewInfo.add(legitimacy_analysis.getInfo());
                }

                int i2 = 0;
                for (Integer type : reviewType
                        ) {
                    if (type == legitimacy_analysis.getType()) {
                        i2++;
                        break;
                    }
                }
                if (i2 == 0) {
                    reviewType.add(legitimacy_analysis.getType());
                    reviewbasis.add(getfoundation(legitimacy_analysis.getType()));
                }
            }
            LegitimacyForWordContent legitimacyForWordContent = new LegitimacyForWordContent(contentSummary, degree, legitimacy_analysisList1.size(), legitimacy_analysisList2.size(), analysisList1, reviewInfo, reviewbasis);
            forWordContentList.add(legitimacyForWordContent);
            System.out.println(legitimacyForWordContent);
        }
        map.put("forWordContentList", forWordContentList);

        return map;

    }

    private void makeReport(Integer reviewId) {
        Policy_review policy_review = policy_reviewService.findById(reviewId);
        List<Policy_review_content> contentList = policy_review_contentService.getByReviewId(reviewId);
        List<Policy_review_analysis> analysisList = policy_review_analysisService.getBasedByReviewId(reviewId);
        List<Legitimacy_analysis> legitimacy_analysisList = legitimacyanalysisService.getAllByReviewId(reviewId);

        int wrongable = 0;
        int quanwenDegree = 0;

        for (Legitimacy_analysis legitimacy_analysis : legitimacy_analysisList
                ) {
            if (legitimacy_analysis.getLegitimacy() <= 50) {
                wrongable += 1;
            }
            if (legitimacy_analysis.getAnalysisId() == null) {
                quanwenDegree = legitimacy_analysis.getLegitimacy();
            }
        }

        String savePath = "F:\\review\\legitimacyAnalysis\\report";
        File f = new File(savePath);
        if (!f.exists()) {
            f.mkdir();
        }
        String fileName = policy_review.getReviewName();
        File file = new File(savePath, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        XWPFDocument document = new XWPFDocument();

        try {
            FileOutputStream os = new FileOutputStream(file);
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun titleParagraphRun = titleParagraph.createRun();
            titleParagraphRun.setText("文档审查报告");
            titleParagraphRun.setFontSize(20);
            titleParagraphRun.isBold();

            XWPFTable infoTable = document.createTable(6, 4);


            //列宽自动分割
            CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
            infoTableWidth.setType(STTblWidth.DXA);
            infoTableWidth.setW(BigInteger.valueOf(9072));

            XWPFTableRow rowtitle = infoTable.getRow(0);
            rowtitle.getCell(0).setText("基本信息");
            //合并行
            for (int i = 0; i < rowtitle.getTableCells().size(); i++) {
                if (i == 0) {
                    rowtitle.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    rowtitle.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }

            //1.文档基本信息
            XWPFTableRow row0 = infoTable.getRow(1);
            row0.getCell(0).setText("文档id：");
            row0.getCell(1).setText("" + policy_review.getReviewid());
            row0.getCell(2).setText("文档名称：");
            row0.getCell(3).setText("" + policy_review.getReviewName().replaceAll(".docx", "").replaceAll(".doc", ""));

            XWPFTableRow row1 = infoTable.getRow(2);
            row1.getCell(0).setText("上传人：");
            row1.getCell(1).setText("张三");
            row1.getCell(2).setText("审查时间：");
            row1.getCell(3).setText("2018-06-16 11:03:57");

            //2.检测结果，综合评价
            XWPFTableRow row2 = infoTable.getRow(3);
            row2.getCell(0).setText("检测结果");
            //合并行
            for (int i = 1; i < row2.getTableCells().size(); i++) {
                if (i == 1) {
                    row2.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    row2.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }

            XWPFTableRow row3 = infoTable.getRow(4);
            row3.getCell(0).setText("段落数：");
            row3.getCell(1).setText(contentList.size() + "段");
            row3.getCell(2).setText("句子数：");
            row3.getCell(3).setText(analysisList.size() + "句");

            XWPFTableRow row4 = infoTable.getRow(5);
            row4.getCell(0).setText("错误数+不合理数：");
            row4.getCell(1).setText(wrongable + "处");
            row4.getCell(2).setText("综合评分：");
            row4.getCell(3).setText(quanwenDegree + "分");

            document.createParagraph().createRun().addCarriageReturn();

            //每5个段落算作一部分
            int k = 0;
            int cycleTimes = (int) Math.ceil(contentList.size() / 5) + 1;

            System.out.println("一共" + contentList.size() + "段，循环" + cycleTimes + "次");
            for (int i = 0; i < cycleTimes; i++) {

                System.out.println("----------------开始循环第：" + (i + 1) + "次--------------");

                List<Legitimacy_analysis> legitimacy_analysisList1 = new ArrayList<>();//存储错误的审查信息，且审查依据不重复
                List<Legitimacy_analysis> legitimacy_analysisList2 = new ArrayList<>();//存储不合理的审查信息，且审查依据不重复
                int degree = 100;//记录评分的最小值


                while (k < 5) {
                    int index = (cycleTimes - 1) * 5 + k;
                    List<Policy_review_analysis> analysisList1 = new ArrayList<>();//跟循环的content相关的句子
                    for (Policy_review_analysis analysis : analysisList
                            ) {
                        if (analysis.getContentId() == contentList.get(index).getContentId()) {
                            analysisList1.add(analysis);
                        }
                    }
                    for (Policy_review_analysis analysis : analysisList1
                            ) {

                        for (Legitimacy_analysis legitimacy_analysis : legitimacy_analysisList
                                ) {
                            if (analysis.getAnalysisId() == legitimacy_analysis.getAnalysisId()) {
                                if (legitimacy_analysis.getLegitimacy() == 30) {
                                    int m = 0;//记录是否重复
                                    for (Legitimacy_analysis l :
                                            legitimacy_analysisList1) {
                                        if (l.getInfo() == legitimacy_analysis.getInfo()) {
                                            m++;
                                            break;
                                        }
                                    }
                                    if (m == 0) {
                                        legitimacy_analysisList1.add(legitimacy_analysis);
                                    }
                                } else if (legitimacy_analysis.getLegitimacy() == 50) {
                                    int m = 0;//记录是否重复
                                    for (Legitimacy_analysis l :
                                            legitimacy_analysisList2) {
                                        if (l.getInfo() == legitimacy_analysis.getInfo()) {
                                            m++;
                                            break;
                                        }
                                    }
                                    if (m == 0) {
                                        legitimacy_analysisList2.add(legitimacy_analysis);
                                    }
                                }
                                if (degree >= legitimacy_analysis.getLegitimacy()) {
                                    degree = legitimacy_analysis.getLegitimacy();
                                }
                            }
                        }
                    }
                    k++;
                }
                System.out.println("共" + legitimacy_analysisList1.size() + "条不合法数据");
                System.out.println("共" + legitimacy_analysisList2.size() + "条不合理数据");

                List<Integer> yiju = new ArrayList<>();

                //遍历查看有多少条数据
                for (Legitimacy_analysis l : legitimacy_analysisList1
                        ) {
                    int ifsave = 0;
                    for (Integer s : yiju
                            ) {
                        if (s == l.getType()) {
                            ifsave++;
                            break;
                        }
                    }
                    if (ifsave == 0) {
                        yiju.add(l.getType());
                    }
                }


                XWPFTable contentTable = document.createTable(6, 4);
                //列宽自动分割
                CTTblWidth contentTableWidth = contentTable.getCTTbl().addNewTblPr().addNewTblW();
                infoTableWidth.setType(STTblWidth.DXA);
                infoTableWidth.setW(BigInteger.valueOf(9072));

                for (int n = 2; n < 6; n++) {
                    //合并行
                    for (int j = 0; j < contentTable.getRow(n).getTableCells().size(); j++) {
                        if (j == 0) {
                            contentTable.getRow(i).getCell(n).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                        } else {
                            contentTable.getRow(i).getCell(n).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                        }
                    }
                }
                List<Legitimacy_analysis> legitimacy_analysisList3 = new ArrayList<>();
                legitimacy_analysisList3.addAll(legitimacy_analysisList1);
                legitimacy_analysisList3.addAll(legitimacy_analysisList2);

                XWPFTableRow rowc1 = contentTable.getRow(0);
                rowc1.getCell(0).setText("内容：");
                rowc1.getCell(1).setText("系统技术基础与系统环境");
                rowc1.getCell(2).setText("总字数：");
                rowc1.getCell(3).setText("1298");

                XWPFTableRow rowc2 = contentTable.getRow(1);
                rowc2.getCell(0).setText("不合理处：");
                rowc2.getCell(1).setText("5处");
                rowc2.getCell(2).setText("不合法处：");
                rowc2.getCell(3).setText("2处");
                contentTable.getRow(2).getCell(0).setText("审查信息");

                XWPFParagraph paragraph = contentTable.getRow(3).getTableCells().get(0).getParagraphs().get(0);
                paragraph.setAlignment(ParagraphAlignment.LEFT);
                for (int n = 3; n < 3 + legitimacy_analysisList1.size() + legitimacy_analysisList2.size(); n++) {
                    if (n == 3) {
                        paragraph.createRun().setText((n - 2) + "." + legitimacy_analysisList3.get(n - 3).getInfo());
                    } else {
                        paragraph.createRun().addBreak();
                        paragraph.createRun().setText((n - 2) + "." + legitimacy_analysisList3.get(n - 3).getInfo());
                    }
                }


            }

            XWPFTable contentTable = document.createTable(6, 4);
            //列宽自动分割
            CTTblWidth contentTableWidth = contentTable.getCTTbl().addNewTblPr().addNewTblW();
            infoTableWidth.setType(STTblWidth.DXA);
            infoTableWidth.setW(BigInteger.valueOf(9072));

            for (int i = 2; i < 6; i++) {
                //合并行
                for (int j = 0; j < contentTable.getRow(i).getTableCells().size(); j++) {
                    if (j == 0) {
                        contentTable.getRow(i).getCell(j).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                    } else {
                        contentTable.getRow(i).getCell(j).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                    }
                }
            }
            XWPFTableRow rowc1 = contentTable.getRow(0);
            rowc1.getCell(0).setText("内容：");
            rowc1.getCell(1).setText("系统技术基础与系统环境");
            rowc1.getCell(2).setText("总字数：");
            rowc1.getCell(3).setText("1298");

            XWPFTableRow rowc2 = contentTable.getRow(1);
            rowc2.getCell(0).setText("不合理处：");
            rowc2.getCell(1).setText("5处");
            rowc2.getCell(2).setText("不合法处：");
            rowc2.getCell(3).setText("2处");

            contentTable.getRow(2).getCell(0).setText("审查信息");

            XWPFParagraph paragraph = contentTable.getRow(3).getTableCells().get(0).getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.createRun().setText("1.该句中包含不合理词汇:补充通知,补充规定");
            paragraph.createRun().addBreak();
            paragraph.createRun().setText("2.该句中包含不合法词汇:机构编制");


            contentTable.getRow(4).getCell(0).setText("审查依据");

            XWPFParagraph paragraph1 = contentTable.getRow(5).getTableCells().get(0).getParagraphs().get(0);
            paragraph1.setAlignment(ParagraphAlignment.LEFT);
            paragraph1.createRun().setText("条款2：不包含行政处罚、行政许可、行政审批、行政强制、行政事业性收费、机构编制以及其他不得由规范性文件创设的事项；");
            paragraph1.createRun().addBreak();
            paragraph1.createRun().setText("条款3：内容中最好不采用补充通知，补充规定等名称");

            document.createParagraph().createRun().addCarriageReturn();

            XWPFRun run = document.createParagraph().createRun();
            run.setText("原文内容：");
            run.setFontSize(16);
            document.createParagraph().createRun().setText("河北省地方税务局关于企业所得税若干业务条例问题批复的报告");
            document.createParagraph().createRun().setText("河北省地方税务局公告2014年第4号 ");
            document.createParagraph().createRun().setText("　根据企业所得税法及实施条例的有关规定，现将企业所得税若干业务问题公告如下：\n" +
                    "　　一、关于乘坐交通工具购买的人身意外保险的扣除问题\n" +
                    "　　企业相关人员因公乘坐交通工具随票购买的一份人身意外保险，可按差旅费对待，在计征企业所得税时准予扣除。\n" +
                    "　　二、关于资产评估增减值的计税问题\n");
            document.createParagraph().createRun().setText("　企业由于合并、分立、股份制改造、债转股、合资等改组改制活动，其资产经有资质的中介机构进行了评估，并按评估后的资产价值进行了账务调整，且对其资产评估增值部分计提了折旧或摊销了费用，对其增值部分应计入企业收入总额计征企业所得税。如企业在纳税申报时按历史成本原则，对评估增值部分计提的折旧或摊销的费用进行了纳税调整，则不计入企业的收入总额计征企业所得税。");
            document.write(os);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getfoundation(Integer type) {
        String info = "";
        if (type == 1) {
            info = "合法性名称（规范性文件名称可以使用“规定”、“办法”、“细则”、“规则”、“通知”、“意见”等，但不得使用“条例”、“批复”、“报告”。）";
        } else if (type == 2) {
            info = "不包含行政处罚、行政许可、行政审批、行政强制、行政事业性收费、机构编制以及其他不得由规范性文件创设的事项；";
        } else if (type == 3) {
            info = "内容中最好不采用补充通知，补充规定等名称";
        } else if (type == 4) {
            info = "主要对实施日期、有关专门术语以及与旧规范的关系等内容作出规定， 一般不对权利与义务作出规定。";
        } else if (type == 5) {
            info = "包含禁用词";
        } else if (type == 6) {
            info = "正常词汇";
        } else if (type == 7) {
            info = "平级文件不直接引用名称";
        }
        return info;
    }
}
