package stdu.wzw.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * StringתInt
     *
     * @param str
     * @return
     */
    public static int StringToInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            result = 0;
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * Stringתfloat
     *
     * @param str
     * @return
     */
    public static float strToFlo(String str) {
        float i = 0;
        try {
            i = Float.parseFloat(str);
        } catch (Exception e) {
        }
        return i;
    }

    public static void main(String[] args) {
        String a = "100.0";
        System.out.println(strToFlo(a));
    }

    /**
     * ��ȡ��ֵ������ַ�����
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static List<String> splitProperties(String properties) {
        List<String> options = new ArrayList<String>();
        String option = null;
        String[] strings = properties.split(",");
        for (String string : strings) {
            if (!"0".equals(string)) {
                option = string.charAt(3) + "";
                options.add(option);

            }
        }
        return options;

    }

    public static String randomStr(int n) {
        char[] array = {
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v',
                'b', 'n', 'm', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'
        };
        int length = array.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(array[((int) (Math.random() * 1000)) % length]);
        }
        return sb.toString();
    }

    /**
     * 判断字符串中是否包含数字或者汉字
     *
     * @param content
     * @return
     */
    public static boolean judgeContains(String content) {

        //先判断是否包含汉字，毕竟汉字的概率较高
        Pattern p1 = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m1 = p1.matcher(content);
        if (m1.find()) {
            return true;
        }
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(content);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 根据句子的长度返回要生成的关键词的个数
     *
     * @param s
     * @return
     */
    public static int getKeySizeByLength(String s) {
        int size = 0;
        if (s.length() <= 20) {
            size = 1;
        } else if (s.length() <= 50) {
            size = 2;
        } else {
            size = 3;
        }
        return size;
    }

}
