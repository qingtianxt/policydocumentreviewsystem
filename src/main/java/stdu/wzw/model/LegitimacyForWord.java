package stdu.wzw.model;

import lombok.Data;

@Data
public class LegitimacyForWord {

    private Integer reviewId;
    private String review_date;//审查时间
    private String review_name;//文档名称

    private String uploadUser;
    private Integer totalDegree;//综合评分
    private Integer paragraphs;//段落数

    private Integer sentence; //句子数
    private Integer unlawfulNumber; //不合法句子数
    private Integer unlawfulType; //不合法种类

    private Integer unreasonableNumber;//不合理数
    private Integer unreasonableType; //不合理种类

    public LegitimacyForWord(Integer reviewId, String review_date, String review_name, String uploadUser, Integer totalDegree, Integer paragraphs, Integer sentence, Integer unlawfulNumber, Integer unlawfulType, Integer unreasonableNumber, Integer unreasonableType) {
        this.reviewId = reviewId;
        this.review_date = review_date;
        this.review_name = review_name;
        this.uploadUser = uploadUser;
        this.totalDegree = totalDegree;
        this.paragraphs = paragraphs;
        this.sentence = sentence;
        this.unlawfulNumber = unlawfulNumber;
        this.unlawfulType = unlawfulType;
        this.unreasonableNumber = unreasonableNumber;
        this.unreasonableType = unreasonableType;
    }

    public LegitimacyForWord() {
    }
}
