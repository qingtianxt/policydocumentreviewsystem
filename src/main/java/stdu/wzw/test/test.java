package stdu.wzw.test;


import com.hankcs.hanlp.HanLP;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import stdu.wzw.model.Policy_similarity;
import stdu.wzw.utils.StringUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

class SortByName implements Comparator {
    public int compare(Object o1, Object o2) {
        Policy_similarity s1 = (Policy_similarity) o1;
        Policy_similarity s2 = (Policy_similarity) o2;
        // Integer i1 = StringUtil.StringToInt(s1.getSimilarityDegree().replace("%", ""));
        // Integer i2 = StringUtil.StringToInt(s2.getSimilarityDegree().replace("%", ""));
        return s2.getSimilarityDegree().compareTo(s1.getSimilarityDegree());
    }
}

public class test {
    public static void main(String[] args) {
        test7();
    }

    public static void test7() {
        float f = 0.1254f;
        int round = Math.round(f * 100);
        System.out.println(round);
    }

    public static void test6() {
        Policy_similarity p1 = new Policy_similarity(1, 12);
        Policy_similarity p2 = new Policy_similarity(2, 50);
        Policy_similarity p3 = new Policy_similarity(3, 5);
        Policy_similarity p4 = new Policy_similarity(4, 7);
        Policy_similarity p5 = new Policy_similarity(5, 80);
        Policy_similarity p6 = new Policy_similarity(6, 70);
        Policy_similarity p7 = new Policy_similarity(7, 50);

        List<Policy_similarity> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        System.out.println("降序排列");
        Collections.sort(list, new SortByName());
        if (list.size() >= 5) {
            list = list.subList(0, 5);
        }
        for (Policy_similarity p : list
                ) {
            System.out.println(p);
        }
    }

    public static void test5() {
        DecimalFormat df = new DecimalFormat("0%");
        System.out.println(df.format(0.126));
    }

    private static Integer bzi(String fenshu) {
        int i = StringUtil.StringToInt(fenshu.replace("%", ""));
        return i;
    }

    //规定根据长度返回要生成的关键词的个数
    public static void test4() {
        String s = "企业由于合并、分立";
        if (s.length() <= 10) {

        } else if (s.length() <= 15) {

        } else if (s.length() <= 25) {

        } else if (s.length() <= 40) {

        } else {

        }
    }

    /**
     * 测试word段落按照每句话分离
     */
    public static void test3() {
        String s = "企业由于合并、分立、股份制改造、债转股、合资等改组改制活动?其资产经有资质的中介机构进行了评估，并按评估后的资产价值进行了账务调整，且对其资产评估增值部分计提了折旧或摊销了费用，对其增值部分应计入企业收入总额计征企业所得税。如企业在纳税申报时按历史成本原则，对评估增值部分计提的折旧或摊销的费用进行了纳税调整，则不计入企业的收入总额计征企业所得税。";
        String[] split = s.split("。|\\.|？|\\?|!|！");
        System.out.println(split.length);
        for (String s1 : split
                ) {
            System.out.println(s1);
        }
    }

    /**
     * 测试hanlp的基本关键词提取
     */
    public static void test2() {
        String document = "　　二、关于资产评估增减值的计税问题\u000B　　企业由于合并、分立、股份制改造、债转股、合资等改组改制活动，其资产经有资质的中介机构进行了评估，并按评估后的资产价值进行了账务调整，且对其资产评估增值部分计提了折旧或摊销了费用，对其增值部分应计入企业收入总额计征企业所得税。如企业在纳税申报时按历史成本原则，对评估增值部分计提的折旧或摊销的费用进行了纳税调整，则不计入企业的收入总额计征企业所得税。\u000B　　企业资产评估减值部分按历史成本原则处理。";
        List<String> sentenceList = HanLP.extractKeyword(document, 3);
        System.out.println(sentenceList);
    }

    /**
     * .doc文档提取
     */
    public static void test() {
        try {
            // InputStream is = new FileInputStream("F:\\policy\\03河北省科学技术厅关于支持“千人计划”人才在河北创新创业的若干措施.doc");
            InputStream is = new FileInputStream("F:\\policy\\04河北省科学技术厅关于科技支持我省沿海地区率先发展的实施意见.doc");
            WordExtractor wordExtractor = new WordExtractor(is);//使用HWPF组件中WordExtractor类从Word文档中提取文本或段落
            int i = 1;
            for (String word : wordExtractor.getParagraphText()) {//获取段落内容
                String s = word.replaceAll("\r\n", "").replaceAll(" +", "");
                if (s.length() > 1) {
                    System.out.println("第" + i + "段:" + word + "   长度为:" + word.length());
                }
                //word.split(" ")
                i++;
            }
            /*for(String word : wordExtractor.getCommentsText()){
                System.out.println("第"+i+":"+word);
                //word.split(" ")
                i++;
            }*/
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * .docx文档提取
     */
    public static void test1() {
        try {
            InputStream is = new FileInputStream("F:\\policy\\10河北省高新技术企业认定专项审计中介机构认定和管理办法.docx");
            XWPFDocument doc = new XWPFDocument(is);
            List<XWPFParagraph> paras = doc.getParagraphs();
            int i = 0;
            for (XWPFParagraph paragraph : paras) {
                String word = paragraph.getText();
                String s = word.replaceAll("\r\n", "").replaceAll(" +", "");
                if (s.length() > 1) {
                    System.out.println("第" + i + "段:" + word + "   长度为:" + word.length());
                }
                i++;
            }
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
