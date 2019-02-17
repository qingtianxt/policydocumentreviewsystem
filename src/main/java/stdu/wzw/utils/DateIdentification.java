package stdu.wzw.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 针对word文档中的日期，判断内容是否是一个日期，同时格式要求严格，不能包含除日期外其余内容
 */
public class DateIdentification {
    /**
     * 总的方法，通过调用该方法进行总体识别
     *
     * @param date
     * @return
     */
    public static boolean ifWordDate(String date) {
        if (iftype1(date) || iftype2(date) || iftype3(date) || iftype4(date) || iftype5(date) || iftype6(date) || iftype7(date) || iftype8(date) || iftype9(date) || iftype10(date)) {
            return true;
        }
        return false;
    }

    /**
     * 处理格式：2018年11月23日
     *
     * @param date
     * @return
     */
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
     * 处理格式：2018/11/23
     *
     * @param date
     * @return
     */
    private static boolean iftype2(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);
            String[] split = date.split("/");
            if (split.length != 3) {
                return false;
            } else {
                result += split[0] + "/";

                String datemonth = split[1];
                String dateday = split[2];

                String[] split1 = format1.split("/");
                if (datemonth.startsWith("0")) {
                    result += split1[1] + "/";
                } else {
                    result += Integer.parseInt(split1[1]) + "/";
                }
                if (dateday.startsWith("0")) {
                    result += split[2];
                } else {
                    result += Integer.parseInt(split1[2]);
                }
                if (!date.equals(result)) {
                    return false;
                }
            }

        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 处理格式：18.11.23
     *
     * @param date
     * @return
     */
    private static boolean iftype3(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);
            String[] split = date.split("\\.");
            System.out.println(split.length);
            if (split.length != 3) {
                return false;
            } else {
                result += split[0] + ".";

                String datemonth = split[1];
                String dateday = split[2];

                String[] split1 = format1.split("\\.");
                if (datemonth.startsWith("0")) {
                    result += split1[1] + ".";
                } else {
                    result += Integer.parseInt(split1[1]) + ".";
                }
                if (dateday.startsWith("0")) {
                    result += split[2];
                } else {
                    result += Integer.parseInt(split1[2]);
                }
                if (!date.equals(result)) {
                    return false;
                }
            }

        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 2018年11月23日星期五
     *
     * @param date
     * @return
     */
    private static boolean iftype4(String date) {
        String result = "";
        if (date.contains("星期")) {
            String substring = date.substring(0, date.indexOf("星"));
            String substring1 = date.substring(date.indexOf("星"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            Date parse = null;
            try {
                //分析星期的前面部分
                parse = format.parse(substring);
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
                if (!substring.equals(result)) {
                    return false;
                }
                System.out.println(1);
                //分析星期部分
                SimpleDateFormat format2 = new SimpleDateFormat("E");
                Date parse1 = format2.parse(substring1);
                String format3 = format2.format(parse1);
                if (format3.equals(substring1)) {
                    return true;
                }

            } catch (ParseException e) {
            }
        }

        return false;
    }

    /**
     * 处理格式： 2018年11月
     *
     * @param date
     * @return
     */
    private static boolean iftype5(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);

            String datemonth = date.substring(date.indexOf("年") + 1, date.indexOf("月"));
            String m = format1.substring(format1.indexOf("年") + 1, format1.indexOf("月"));

            result += format1.substring(0, format1.indexOf("年") + 1);
            if (datemonth.startsWith("0")) {
                result += m + "月";
            } else {
                int month = Integer.parseInt(m);
                result += month + "月";
            }

            if (!date.equals(format1)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 处理格式：11时9分21秒
     *
     * @param date
     * @return
     */
    private static boolean iftype6(String date) {
        SimpleDateFormat format = new SimpleDateFormat("hh时mm分ss秒");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);

            String datehour = date.substring(0, date.indexOf("时"));
            String dateMin = date.substring(date.indexOf("时") + 1, date.indexOf("分"));
            String datetime = date.substring(date.indexOf("分") + 1, date.indexOf("秒"));

            String h = format1.substring(0, format1.indexOf("时"));
            if (datehour.startsWith("0")) {
                result += h + "时";
            } else {
                int hour = Integer.parseInt(h);
                result += hour + "时";
            }

            String m = format1.substring(format1.indexOf("时") + 1, format1.indexOf("分"));
            if (dateMin.startsWith("0")) {
                result += m + "分";
            } else {
                int minute = Integer.parseInt(m);
                result += minute + "分";
            }
            String s = format1.substring(format1.indexOf("分") + 1, format1.indexOf("秒"));
            if (datetime.startsWith("0")) {
                result += s;
            } else {
                int second = Integer.parseInt(s);
                result += second;
            }
            result += "秒";
            System.out.println(result);
            if (!date.equals(result)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 处理格式：11时13分 23时13分
     *
     * @param date
     * @return
     */
    private static boolean iftype7(String date) {
        SimpleDateFormat format = new SimpleDateFormat("hh时mm分");
        Date parse = null;
        String result = "";
        try {
            parse = format.parse(date);
            String format1 = format.format(parse);

            String datehour = date.substring(0, date.indexOf("时"));
            String dateMin = date.substring(date.indexOf("时") + 1, date.indexOf("分"));

            String h = format1.substring(0, format1.indexOf("时"));
            if (datehour.startsWith("0")) {
                result += h + "时";
            } else {
                int hour = Integer.parseInt(h);
                result += hour + "时";
            }

            String m = format1.substring(format1.indexOf("时") + 1, format1.indexOf("分"));
            if (dateMin.startsWith("0")) {
                result += m + "分";
            } else {
                int minute = Integer.parseInt(m);
                result += minute + "分";
            }
            if (!date.equals(result)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 处理格式：下午11时12分 上午11时12分
     *
     * @param date
     * @return
     */
    private static boolean iftype8(String date) {
        if (date.startsWith("下午") || date.startsWith("上午")) {
            date = date.substring(2);

            SimpleDateFormat format = new SimpleDateFormat("hh时mm分");
            Date parse = null;
            String result = "";
            try {
                parse = format.parse(date);
                String format1 = format.format(parse);

                String datehour = date.substring(0, date.indexOf("时"));
                String dateMin = date.substring(date.indexOf("时") + 1, date.indexOf("分"));

                String h = format1.substring(0, format1.indexOf("时"));
                if (datehour.startsWith("0")) {
                    result += h + "时";
                } else {
                    int hour = Integer.parseInt(h);
                    result += hour + "时";
                }

                String m = format1.substring(format1.indexOf("时") + 1, format1.indexOf("分"));
                if (dateMin.startsWith("0")) {
                    result += m + "分";
                } else {
                    int minute = Integer.parseInt(m);
                    result += minute + "分";
                }
                if (!date.equals(result)) {
                    return false;
                }
            } catch (ParseException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 转化格式：二〇一八年十一月二十三日
     * 如果中文时间可以成功转化为数字时间，算作word日期格式
     *
     * @param date
     * @return
     */
    private static boolean iftype9(String date) {

        try {
            Date date1 = convertymdDate(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 转化格式：二〇一八年十一月
     *
     * @param date
     * @return
     */
    private static boolean iftype10(String date) {

        if (!date.endsWith("月")) {
            return false;
        }
        try {
            Date date1 = convertymDate(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 用来识别格式为：二〇一八年十一月二十三日 可以带星期
     *
     * @param cprq
     * @return
     */
    private static Date convertymdDate(String cprq) {
        if (cprq.contains("星期")) {
            cprq = cprq.substring(0, cprq.indexOf("星期"));
        }
        int yearPos = cprq.indexOf("年");
        int monthPos = cprq.indexOf("月");
        String cnYear = cprq.substring(0, yearPos);
        String cnMonth = cprq.substring(yearPos + 1, monthPos);
        String cnDay = cprq.substring(monthPos + 1, cprq.length() - 1);
        String year = ConvertCnYear(cnYear);
        String month = ConvertCnDateNumber(cnMonth);
        String day = ConvertCnDateNumber(cnDay);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(year));
        c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        return c.getTime();
    }

    /**
     * 用来识别格式为：二〇一八年十一月
     *
     * @param cprq
     * @return
     */
    private static Date convertymDate(String cprq) {
        int yearPos = cprq.indexOf("年");
        int monthPos = cprq.indexOf("月");
        String cnYear = cprq.substring(0, yearPos);
        String cnMonth = cprq.substring(yearPos + 1, monthPos);
        String year = ConvertCnYear(cnYear);
        String month = ConvertCnDateNumber(cnMonth);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(year));
        c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        return c.getTime();
    }

    /**
     * 中文年份转化
     *
     * @param cnYear
     * @return
     */
    private static String ConvertCnYear(String cnYear) {
        if (cnYear.length() == 2)
            return "20" + ConvertCnNumberChar(cnYear);
        else
            return ConvertCnNumberChar(cnYear);
    }

    private static String ConvertCnDateNumber(String cnNumber) {
        if (cnNumber.length() == 1) {
            if (cnNumber.equals("十"))
                return "10";
            else
                return ConvertCnNumberChar(cnNumber);
        } else if (cnNumber.length() == 2) {
            if (cnNumber.startsWith("十")) {
                return "1" + ConvertCnNumberChar(cnNumber.substring(1, 2));
            } else if (cnNumber.endsWith("十")) {
                return ConvertCnNumberChar(cnNumber.substring(0, 1)) + "0";
            } else {
                return ConvertCnNumberChar(cnNumber);
            }
        } else if (cnNumber.length() == 3) {
            return ConvertCnNumberChar(cnNumber.substring(0, 1) + cnNumber.substring(2, 3));
        }
        return null;
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
