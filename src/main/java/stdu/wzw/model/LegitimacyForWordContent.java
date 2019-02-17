package stdu.wzw.model;

import lombok.Data;

import java.util.List;

@Data
public class LegitimacyForWordContent {


    private String content;//摘要
    private Integer paragraphDegree; //阶段分值
    private Integer unlawableNumber; //不合法内容处

    private Integer unreasonableNumber; //不合理内容处
    private List<Policy_review_analysis> analysisList;//阶段内容

    private List<String> reviewInfo;
    private List<String> reviewbasis;

    public LegitimacyForWordContent(String content, Integer paragraphDegree, Integer unlawableNumber, Integer unreasonableNumber, List<Policy_review_analysis> analysisList, List<String> reviewInfo, List<String> reviewbasis) {
        this.content = content;
        this.paragraphDegree = paragraphDegree;
        this.unlawableNumber = unlawableNumber;
        this.unreasonableNumber = unreasonableNumber;
        this.analysisList = analysisList;
        this.reviewInfo = reviewInfo;
        this.reviewbasis = reviewbasis;
    }

    public LegitimacyForWordContent() {
    }
}
