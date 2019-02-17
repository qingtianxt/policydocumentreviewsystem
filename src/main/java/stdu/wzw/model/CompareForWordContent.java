package stdu.wzw.model;

import lombok.Data;

import java.util.List;

/**
 * 为政策报告提供数据支持
 */
@Data
public class CompareForWordContent {

    private List<CompareForWord> compareForWordList;
    private Integer totalSimilarity;

    private String summary;
    private List<Policy_review_analysis> analysisList;

    public CompareForWordContent() {
    }

    public CompareForWordContent(List<CompareForWord> compareForWord, Integer totalSimilarity, String summary, List<Policy_review_analysis> analysisList) {
        this.compareForWordList = compareForWord;
        this.totalSimilarity = totalSimilarity;
        this.summary = summary;
        this.analysisList = analysisList;
    }
}
