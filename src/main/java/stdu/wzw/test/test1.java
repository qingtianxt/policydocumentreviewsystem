package stdu.wzw.test;

/*import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.SynonymTagging;*/
import org.springframework.beans.factory.annotation.Autowired;
import stdu.wzw.mapper.Policy_reviewMapper;
import stdu.wzw.utils.MySimHash;
import stdu.wzw.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.List;

public class test1 {

    @Autowired
    private Policy_reviewMapper policy_reviewMapper;


    public static void main(String[] args) {
    }


    private static void test5() {
        int[][] s = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                s[i][j] = i + j;
            }
        }
        System.out.println(s[0]);
    }

    private static void test4() {
        //int degree = StringUtil.StringToInt(df.format(0.645));
        DecimalFormat df = new DecimalFormat("#.00");
        String replace = df.format(0.633).replace(".", "");
        System.out.println(StringUtil.StringToInt(replace));
    }

    private static void test3() {
        String s1 = "  借鉴hashmap算法找出可以hash的key值，因为我们使用的simhash是局部敏感哈希，这个算法的特点是只要相似的字符串只有个别的位数是有差别变化。那这样我们可以推断两个相似的文本，至少有16位的simhash是一样的。具体选择16位、8位、4位，大家根据自己的数据测试选择，虽然比较的位数越小越精准，但是空间会变大。分为4个16位段的存储空间是单独simhash存储空间的4倍。之前算出5000w数据是 382 Mb，扩大4倍1.5G左右，还可以接受：）  通过这样计算，我们的simhash查找过程全部降到了1毫秒以下。就加了一个hash效果这么厉害？我们可以算一下，原来是5000w次顺序比较，现在是少了2的16次方比较，前面16位变成了hash查找。后面的顺序比较的个数是多少？ 2^16 = 65536， 5000w/65536 = 763 次。。。。实际最后链表比较的数据也才 763次！所以效率大大提高！  到目前第一点降到3.6毫秒、支持5000w数据相似度比较做完了。还有第二点同一时刻发出的文本如果重复也只能保留一条和短文本相识度比较怎么解决。其实上面的问题解决了，这两个就不是什么问题了。  之前的评估一直都是按照线性计算来估计的，就算有多线程提交相似度计算比较，我们提供相似度计算服务器也需要线性计算。比如同时客户端发送过来两条需要比较相似度的请求，在服务器这边都进行了一个排队处理，一个接着一个，第一个处理完了在处理第二个，等到第一个处理完了也就加入了simhash库。所以只要服务端加了队列，就不存在同时请求不能判断的情况。 simhash如何处理短文本？换一种思路，simhash可以作为局部敏感哈希第一次计算缩小整个比较的范围，等到我们只有比较700多次比较时，就算使用我们之前精准度高计算很慢的编辑距离也可以搞定。当然如果觉得慢了，也可以使用余弦夹角等效率稍微高点的相似度算法";
        String s2 = "  1、分词，把需要判断文本分词形成这个文章的特征单词。 最后形成去掉噪音词的单词序列并为每个词加上权重，我们假设权重分为5个级别（1~5）。只要相似的字符串只有个别的位数是有差别变化。那这样我们可以推断两个相似的文本， 比如：“ 美国“51区”雇员称内部有9架飞碟，曾看见灰色外星人 ” ==> 分词后为 “ 美国（4） 51区（5） 雇员（3） 称（1） 内部（2） 有（1） 9架（3） 飞碟（5） 曾（1） 看见（3） 灰色（4） 外星人（5）”， 括号里是代表单词在整个句子里重要程度，数字越大越重要。 2、hash，通过hash算法把每个词变成hash值， 比如“美国”通过hash算法计算为 100101, “51区”通过hash算法计算为 101011。 这样我们的字符串就变成了一串串数字，还记得文章开头说过的吗，要把文章变为数字计算才能提高相似度计算性能，现在是降维过程进行时。 3、加权，通过 2步骤的hash生成结果，需要按照单词的权重形成加权数字串， 比如“美国”的hash值为“100101”，通过加权计算为“4 -4 -4 4 -4 4”；“51区”的hash值为“101011”，通过加权计算为 “ 5 -5 5 -5 5 5”。 4、合并，把上面各个单词算出来的序列值累加，变成只有一个序列串。 比如 “美国”的 “4 -4 -4 4 -4 4”，“51区”的 “ 5 -5 5 -5 5 5”， 把每一位进行累加， “4+5 -4+-5 -4+5 4+-5 -4+5 4+5” ==》 “9 -9 1 -1 1 9”。 这里作为示例只算了两个单词的，真实计算需要把所有单词的序列串累加。 5、降维，把4步算出来的 “9 -9 1 -1 1 9” 变成 0 1 串，形成我们最终的simhash签名。 如果每一位大于0 记为 1，小于0 是统优先公司";
        MySimHash m1 = new MySimHash(s1, 64);
        MySimHash m2 = new MySimHash(s2, 64);
        double semblance = m1.getSemblance(m2);
        System.out.println(semblance);
    }

   /* private static void test2() {
        String s = "如果交易合同或协议中规定租赁期限跨年度，且租金提前一次性支付的，根据收入与费用配比原则，在租赁期内，分期均匀计入相关年度收入";
        List<Word> words = WordSegmenter.segWithStopWords(s);
        SynonymTagging.process(words);
        for (Word w :
                words) {
            System.out.println(w.getText());
            if (null != w.getSynonym() && w.getSynonym().size() > 0) {
                List<Word> synonym = w.getSynonym();
                System.out.println(w.getText() + ":  " + synonym);
            } else {
                System.out.println("无同义词推荐");
            }

        }
    }*/
}
