package stdu.wzw.test;

import com.ansj.vec.Word2VEC;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

public class SimilarityAnalysis {
    public static void main(String[] args) {
        String word1 = "　　（三）加强示范引导。积极指导推动试点示范工作，交流各地、各有关部门的好经验、好做法，对可复制、可推广的经验和模式及时进行总结推广，发挥促进科技成果转移转化的示范带动作用。引导全社会关心和支持科技成果转移转化，营造有利于科技成果转移转化的良好社会氛围。";
        // String word2 = "　　（一）加强组织领导。省各有关部门要根据职能和任务分工，加强对上衔接、省地协同，强化重点任务的统筹部署和创新资源的统筹配置，形成推进科技成果转移转化的强大合力。各市、县（市、区）人民政府要将科技成果转移转化工作摆上重要位置，结合实际制定具体实施方案，明确工作推进路线图和时间表，逐级细化分解任务，切实加大资金投入、政策支持和条件保障力度。省科技厅要会同相关部门对本方案的落实情况进行跟踪分析和督促指导。";
        String word2 = word1;
        compareSimilarity(word1, word2);
    }

    private static void compareSimilarity(String word1, String word2) {
        Word2VEC vec =new Word2VEC();
    }
}
