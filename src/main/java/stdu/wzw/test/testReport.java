package stdu.wzw.test;

import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.List;

public class testReport {

    public static void main(String[] args) {
        makeReport();
    }

    private static void makeReport() {
        String savePath = "F:\\review\\legitimacyAnalysis\\report";
        File f = new File(savePath);
        if (!f.exists()) {
            f.mkdir();
        }
        String fileName = "1.docx";
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
            row0.getCell(1).setText("126");
            row0.getCell(2).setText("文档名称：");
            row0.getCell(3).setText("二手图书交易网站的设计与实现");

            XWPFTableRow row1 = infoTable.getRow(2);
            row1.getCell(0).setText("上传人：");
            row1.getCell(1).setText("张三");
            row1.getCell(2).setText("审查时间：");
            row1.getCell(3).setText("2015-06-16 11:03:57");

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
            row3.getCell(1).setText("67段");
            row3.getCell(2).setText("句子数：");
            row3.getCell(3).setText("80句");

            XWPFTableRow row4 = infoTable.getRow(5);
            row4.getCell(0).setText("错误数：");
            row4.getCell(1).setText("5处");
            row4.getCell(2).setText("综合评分：");
            row4.getCell(3).setText("70分");

            document.createParagraph().createRun().addCarriageReturn();

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


}
