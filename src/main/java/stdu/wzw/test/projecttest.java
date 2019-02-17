package stdu.wzw.test;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
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
import stdu.wzw.utils.DateIdentification;
import stdu.wzw.utils.SentenceTypeAnalysis;
import stdu.wzw.utils.SimilarityAnalysisUtils;
import stdu.wzw.utils.StringUtil;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class projecttest {

    public static void main(String[] args) {
        getTimeRelated();
    }
    private static  String getTimeRelated(){
        float degree = SimilarityAnalysisUtils.analysisByCos("18河北省省级科技计划项目管理办法","11-关于调整国家科技计划和公益性行业科研专项经费管理办法若干规定的通知");
        int round = Math.round(degree * 100);
        System.out.println(round);

        String s="";
        return "1";
    }


    private static boolean ifPolicySymbol(String word) {
        //判断有没有括号，有括号是复杂文号
        word = word.trim().replaceAll("/r/n", "");
        System.out.println(word);
        if ((word.startsWith("（") || word.startsWith("(")) && (word.endsWith(")") || word.endsWith("）"))) {
            if (word.contains("日") && word.contains("号")) {
                try {
                    String date = "";
                    if (word.contains("(")) {
                        date = word.substring(word.indexOf("(") + 1, word.indexOf("日") + 1);
                    } else {
                        date = word.substring(word.indexOf("（") + 1, word.indexOf("日") + 1);
                    }

                    if (iftype1(date)) {
                        if ((word.contains("[") || word.contains("〔")) && (word.contains("]") || word.contains("〕"))) {
                            if (word.contains("]")) {
                                if (isNumeric(word.substring(word.indexOf("]") + 1, word.indexOf("号")).trim())) {
                                    return true;
                                }
                            } else {
                                String number = word.substring(word.indexOf("〕") + 1, word.indexOf("号")).trim();
                                if (isNumeric(number)) {
                                    return true;
                                }
                            }

                        } else if (word.contains("第")) {
                            if (isNumeric(word.substring(word.indexOf("第") + 1, word.indexOf("号")).trim())) {
                                return true;
                            }
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            if (word.endsWith("号") && (word.contains("[") || word.contains("〔"))) {
                try {
                    if (word.contains("[")) {
                        String ifyear = word.substring(word.indexOf("[") + 1, word.indexOf("]"));
                        try {
                            new SimpleDateFormat("yyyy").parse(ifyear);
                            if (isNumeric(word.substring(word.indexOf("]") + 1, word.indexOf("号")).trim())) {
                                return true;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String ifyear = word.substring(word.indexOf("〔") + 1, word.indexOf("〕"));
                        try {
                            new SimpleDateFormat("yyyy").parse(ifyear);
                            String s = word.substring(word.indexOf("〕") + 1, word.indexOf("号")).trim();
                            if (isNumeric(s)) {
                                return true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return false;
    }

    //判断一个字符串是否为数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean iftype1(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);
            String datemonth = date.substring(date.indexOf("年") + 1, date.indexOf("月"));
            String dateday = date.substring(date.indexOf("月") + 1, date.indexOf("日"));
            result += format1.substring(0, format1.indexOf("年") + 1);
            String m = format1.substring(format1.indexOf("年") + 1, format1.indexOf("月"));

            if (datemonth.startsWith("0")) {
                result += m + "月";
            } else {
                int month = Integer.parseInt(m);
                result += month + "月";
            }
            String d = format1.substring(format1.indexOf("月") + 1, format1.indexOf("日"));
            if (dateday.startsWith("0")) {
                result += d;
            } else {
                int day = Integer.parseInt(d);
                result += day;
            }
            result += "日";
            if (!date.equals(result)) {
                return false;
            }

        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private static void solveFileByUrl1(String url) {
        List<String> list = new ArrayList<>();
        File file = new File(url);
        int j = 0;
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
                            //解决标题是两行，中间有回车的情况,存储第一行内容的属性，和第二行进行对比，如果两个属性一致，初步判断为两部分内容一致，这时需要将第二段和第一段一起视作标题
                            if (i == 0) {
                                int count = 0;
                                while (true) {
                                    CharacterRun run = para.getCharacterRun(count);// 此characterrun并非一个字符，而是一类字符，例如“数据挖掘”，前两个字为加粗，后两个字不加粗，那么“数据”　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　和“挖掘”会存在两个characterrun中
                                    run1 = para.getCharacterRun(0);
                                    count++;
                                    if (run.getEndOffset() == para.getEndOffset()) {//到达段落末尾
                                        break;
                                    }
                                }
                            }
                            if (i == 1) {
                                int count = 0;
                                while (true) {
                                    CharacterRun run = para.getCharacterRun(count);// 此characterrun并非一个字符，而是一类字符，例如“数据挖掘”，前两个字为加粗，后两个字不加粗，那么“数据”　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　和“挖掘”会存在两个characterrun中
                                    run2 = para.getCharacterRun(0);

                                    System.out.println("color ------" + run.getColor());// 字体颜色
                                    System.out.println("font name---" + run.getFontName());// 字体类型
                                    System.out.println("font size---" + run.getFontSize());// 字体大小
                                    System.out.println("bold -------" + run.isBold());// 是否加粗
                                    System.out.println("italic -----" + run.isItalic());// 是否斜体字
                                    count++;
                                    if (run.getEndOffset() == para.getEndOffset()) {//到达段落末尾
                                        break;
                                    }
                                }
                                if (run1.getColor() == run2.getColor() && run1.getFontName() == run2.getFontName() && run1.getFontSize() == run2.getFontSize() && run1.isItalic() == run2.isItalic()) {

                                }
                            }
                            System.out.println("第" + (i + 1) + "段：" + paratext);
                        }
                        j++;
                    }
                } else if (url.endsWith(".docx")) {
                    XWPFDocument doc = new XWPFDocument(is);
                    List<XWPFParagraph> paras = doc.getParagraphs();
                    for (XWPFParagraph paragraph : paras) {

                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run :
                                runs) {
                            System.out.println("alignment --" + paragraph.getAlignment());//只有左对齐，右对齐和居中对齐
                            System.out.println("text -------" + run.getText(0));
                            System.out.println("font name --" + run.getFontFamily());
                            System.out.println("color ------" + run.getColor());
                            System.out.println("font size --" + run.getFontSize());
                        }

                        String word = paragraph.getText();
                        String s = word.trim().replaceAll("\r\n", "");

                        if (s.length() > 0) {
                            list.add(word);
                            System.out.println("---------------------------------------");
                        }

                    }
                }
                is.close();
                //  policy_contentService.insertList(list, policyId);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 包含字体解析
     *
     * @param url
     */
    private static void solveFileByUrl(String url) {
        List<String> list = new ArrayList<>();
        File file = new File(url);
        if (file.exists()) {
            try {
                InputStream is = new FileInputStream(file);

                if (url.endsWith(".doc")) {
                    POIFSFileSystem fs = new POIFSFileSystem(is);
                    HWPFDocument document = new HWPFDocument(fs);
                    Range head = document.getHeaderStoryRange();
                    for (int i = 0; i < head.numCharacterRuns(); i++) {
                        System.out.println(head.getCharacterRun(i).text());
                    }
                    System.out.println("===========================================");
                    Range range = document.getRange();
                    for (int i = 0; i < range.numParagraphs(); i++) {
                        System.out.println("=================第" + (i + 1) + "段======================");
                        Paragraph para = range.getParagraph(i);
                        int count = 0;
                        while (true) {
                            CharacterRun run = para.getCharacterRun(count);// 此characterrun并非一个字符，而是一类字符，例如“数据挖掘”，前两个字为加粗，后两个字不加粗，那么“数据”　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　和“挖掘”会存在两个characterrun中

                            System.out.println("color ------" + run.getColor());// 字体颜色
                            System.out.println("font name---" + run.getFontName());// 字体类型
                            System.out.println("font size---" + run.getFontSize());// 字体大小
                            System.out.println("text -------" + run.text());// 文本信息
                            System.out.println("bold -------" + run.isBold());// 是否加粗
                            System.out.println("italic -----" + run.isItalic());// 是否斜体字
                            System.out.println("algnment ---" + para.getJustification());//对齐方式，0为左对齐，1为居中，2为右对齐，3为两端对齐
                            count++;
                            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                            if (run.getEndOffset() == para.getEndOffset()) {//到达段落末尾
                                break;
                            }
                        }
                        // 获取第i段
                        String paratext = para.text().trim().replaceAll("\r\n", "");
                        System.out.println("paratext:" + paratext);
                        if (!"".equals(paratext) && null != paratext && paratext.length() > 0) {

                            System.out.println("--------------分段--------------");
                        } else {
                            System.out.println("内容为空");
                        }

                    }


                } else if (url.endsWith(".docx")) {
                    XWPFDocument doc = new XWPFDocument(is);
                    int i = 0;
                    List<XWPFParagraph> paras = doc.getParagraphs();

                    for (XWPFParagraph paragraph : paras) {
                        System.out.println("=================第" + (i + 1) + "段======================");
                        System.out.println("段落内容:" + paragraph.getText());
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run :
                                runs) {
                            //  paragraph.setAlignment();
                            System.out.println("alignment --" + paragraph.getAlignment());//只有左对齐，右对齐和居中对齐
                            System.out.println("text -------" + run.getText(0));
                            System.out.println("font name --" + run.getFontFamily());
                            System.out.println("color ------" + run.getColor());
                            System.out.println("font size --" + run.getFontSize());
                        }
                        String word = paragraph.getText();
                        String s = word.trim().replaceAll("\r\n", "");
                        if (s.length() > 1) {

                        }
                        i++;
                    }
                }
                is.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
