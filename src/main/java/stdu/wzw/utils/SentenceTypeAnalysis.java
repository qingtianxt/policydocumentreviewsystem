package stdu.wzw.utils;

import stdu.wzw.model.Department;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * word类型的句子类型分析，适用于规范性文件
 */
public class SentenceTypeAnalysis {
    public static int analysisType(String analysisWord, List<Department> departmentList, List<String> placeList, int lastSentence, boolean istransform, String fontName, boolean bold) {

        if (DateIdentification.ifWordDate(analysisWord)) {
            return 2;
        } else if (ifDepartment(analysisWord, departmentList)) {
            return 3;
        } else if (ifPlace(analysisWord, placeList)) {
            return 4;
        } else if (ifPolicySymbol(analysisWord)) {
            return 5;
        } else if (ifEffective(lastSentence, analysisWord)) {
            return 6;
        } else if (ifkeyElements(analysisWord) || ifEnclosure(analysisWord)) {
            return 7;
        } else if (istransform == false && (fontName.equals("黑体") || bold == true)) {
            return 8;
        } else {
            return 9;
        }

    }


    /**
     * 判断一段话是不是政策文号
     *
     * @param word
     * @return
     */
    private static boolean ifPolicySymbol(String word) {
        //判断有没有括号，有括号是复杂文号
        word = word.trim().replaceAll("/r/n", "");
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


    /**
     * 判断这句话是不是一个部门的名称，或者主体包含是一个名称,据观察，有同时包含多个部门的情况
     *
     * @return
     */
    private static boolean ifDepartment(String analysisWord, List<Department> departmentList) {
        if (analysisWord.contains("部门:") || analysisWord.contains("部门：")) {
            return true;
        }
        String[] split = analysisWord.split(" ");
        for (Department d : departmentList
                ) {
            //当字符串不能切割时，认定只有一个部门
            if (split.length == 1) {
                if (d.getDepartmentName().equals(analysisWord)) {
                    return true;
                }
            }
            //长度大于1时，认定为可能有多个部门，然后部门中有一个符合，就返回该内容为部门。
            else if (split.length > 1) {
                for (String s : split) {
                    if (d.getDepartmentName().equals(s)) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断一句话是不是地址
     *
     * @param analysisWord
     * @return
     */
    private static boolean ifPlace(String analysisWord, List<String> list) {
        if (analysisWord.contains("地点：") || analysisWord.contains("地点:")) {
            return true;
        }
        String[] split = analysisWord.split(" ");
        for (String place : list
                ) {
            if (split.length == 1) {
                if (place.equals(analysisWord)) {
                    return true;
                }
            } else if (split.length > 1) {
                for (String s : split
                        ) {
                    if (place.equals(s)) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是不是施行日期，等内容
     *
     * @param lastSentence
     * @param analysisWord
     * @return
     */
    private static boolean ifEffective(int lastSentence, String analysisWord) {
        int analysisResult = 0;
        if (lastSentence == 1) {
            analysisResult += 25;
        }
        if (analysisWord.contains("施行") || analysisWord.contains("实施") || analysisWord.contains("废止") || analysisWord.contains("试行") || analysisWord.contains("执行")) {
            analysisResult += 25;
        }
        if (analysisWord.contains("印发之日") || analysisWord.contains("发布之日") || analysisWord.contains("有效期") || analysisWord.contains("废止日期") || analysisWord.contains("已经发布")) {
            analysisResult += 25;
        }
        if ((analysisWord.contains("自") && analysisWord.contains("起")) || analysisWord.contains("同时") || analysisWord.contains("至")) {
            analysisResult += 25;
        }
        if (analysisResult >= 50) {
            return true;
        }
        return false;
    }

    /**
     * 判断一段内容是不是方案要素
     *
     * @param analysisWord
     * @return
     */
    private static boolean ifkeyElements(String analysisWord) {
        if ((analysisWord.contains(":") || analysisWord.contains("：")) && (analysisWord.contains("时间") || analysisWord.contains("手机") || analysisWord.contains("电话") || analysisWord.contains("传真") || analysisWord.contains("地点") || analysisWord.contains("邮编") || analysisWord.contains("联系人") || analysisWord.contains("@") || analysisWord.contains("人员") || analysisWord.contains("成员") || analysisWord.contains("单位") || analysisWord.contains("项目"))) {
            return true;
        }
        return false;
    }

    /**
     * 判断一句话是否是附件内容:判断规则：字符串包含附件，然后后面是数字或者是中文数字
     *
     * @param analysisWord
     * @return
     */
    public static boolean ifEnclosure(String analysisWord) {
        if (analysisWord.contains("附件")) {

            if (analysisWord.equals("附件：") || analysisWord.equals("附件")) {
                return true;
            }

            String possibleNumber = analysisWord.replaceAll("附件", "");
            if (possibleNumber != null && !"".equals(possibleNumber.trim())) {
                if (possibleNumber.matches("^[0-9]*$")) {
                    return true;
                }
                possibleNumber = possibleNumber.replaceAll("十", "").replaceAll("百", "").replaceAll("千", "").replaceAll("万", "").replaceAll("亿", "");

                String s = ConvertCnNumberChar(possibleNumber);
                if (s.matches("^[0-9]*$")) {
                    return true;
                }

            }
        }
        return false;
    }


    private static String ConvertCnNumberChar(String cnNumberStr) {
        String ALL_CN_NUMBER = "○〇零一二三四五六七八九";
        String ALL_NUMBER = "000123456789";
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < cnNumberStr.length(); i++) {
            char c = cnNumberStr.charAt(i);
            int index = ALL_CN_NUMBER.indexOf(c);
            if (index != -1) {
                buf.append(ALL_NUMBER.charAt(index));
            } else {
                buf.append(cnNumberStr.charAt(i));
            }
        }
        return buf.toString();
    }
}
