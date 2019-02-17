package stdu.wzw.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.seg.common.Term;
import stdu.wzw.utils.sentenceSimilarity.Segment;
import stdu.wzw.utils.sentenceSimilarity.Word2Vec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 相似度分析工具类
 */
public class SimilarityAnalysisUtils {


    public static float semanticsSimilarity(Word2Vec vec, String s1, String s2) throws Exception {
        List<String> wordList1 = Segment.getWords(s1);
        List<String> wordList2 = Segment.getWords(s2);
        float[] weightArray1 = Segment.getPOSWeightArray(Segment.getPOS(s1));
        float[] weightArray2 = Segment.getPOSWeightArray(Segment.getPOS(s2));
        float degree = vec.sentenceSimilarity(wordList1, wordList2, weightArray1, weightArray2);
        return degree;
    }

    /**
     * 余弦相似度分析
     *
     * @param str1
     * @param str2
     * @return
     */
    public static float analysisByCos(String str1, String str2) {
        List<String> list1 = stringParticiple(str1);
        List<String> list2 = stringParticiple(str2);

        return getDoubleStrForCosValue(getStringFrequency(list1, list2));
    }

    /**
     * 获取两组向量的余弦值
     *
     * @return
     */
    public static float getDoubleStrForCosValue(int[][] ints) {
        BigDecimal fzSum = new BigDecimal(0);
        BigDecimal fmSum = new BigDecimal(0);

        int num = ints[0].length;
        for (int i = 0; i < num; i++) {
            BigDecimal adb = new BigDecimal(ints[0][i]).multiply(new BigDecimal(ints[1][i]));
            fzSum = fzSum.add(adb);
        }
        BigDecimal seq1SumBigDecimal = new BigDecimal(0);
        BigDecimal seq2SumBigDecimal = new BigDecimal(0);

        for (int i = 0; i < num; i++) {
            seq1SumBigDecimal = seq1SumBigDecimal.add(new BigDecimal(Math.pow(ints[0][i], 2)));
            seq2SumBigDecimal = seq2SumBigDecimal.add(new BigDecimal(Math.pow(ints[1][i], 2)));
        }
        double sqrt1 = Math.sqrt(seq1SumBigDecimal.doubleValue());
        double sqrt2 = Math.sqrt(seq2SumBigDecimal.doubleValue());

        fmSum = new BigDecimal(sqrt1).multiply(new BigDecimal(sqrt2));

        return fzSum.divide(fmSum, 10, RoundingMode.HALF_UP).floatValue();
    }

    /**
     * 获取两组字符串的词频向量
     *
     * @param str1
     * @param str2
     * @return
     */
    public static int[][] getStringFrequency(List<String> str1, List<String> str2) {
        Set<String> cnCet = new HashSet<>();
        cnCet.addAll(str1);
        cnCet.addAll(str2);

        int[][] res = new int[2][cnCet.size()];
        Iterator<String> iterator = cnCet.iterator();
        int i = 0;
        //获取词频
        while (iterator.hasNext()) {
            String word = iterator.next().toString();
            int s1 = 0;
            int s2 = 0;
            for (String str : str1
                    ) {
                if (word.equals(str)) {
                    s1++;
                }
            }
            res[0][i] = s1;
            for (String str : str2
                    ) {
                if (word.equals(str)) {
                    s2++;
                }
            }
            res[1][i] = s2;
            i++;
        }
        return res;
    }


    /**
     * 中文分词
     *
     * @param text
     * @return
     */
    private static List<String> stringParticiple(String text) {
        List<String> list = new ArrayList<>();
        List<Term> termList = HanLP.segment(text);
        for (Term term : termList
                ) {
            list.add(term.word);
        }
        return list;
    }
}
