package stdu.wzw.test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class sqlutil {
    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<>();
        File file = new File("F:\\area.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str = bufferedReader.readLine();
        String result = "";
        while (null != str && !"".equals(str)) {
            if (str.endsWith(",")) {
                result = "insert into area values(" + str + "null)";
            } else {
                result = "insert into area values(" + str + ")";
            }
            System.out.println(result);
            list.add(result);
            result = "";
            str = bufferedReader.readLine();
        }
        bufferedReader.close();
        File file1 = new File("F:\\area1.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
        for (String s : list
                ) {
            bw.write(s);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
