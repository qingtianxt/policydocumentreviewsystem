package stdu.wzw.model;

import lombok.Data;

@Data
public class Policy_review_analysis {
    private Integer analysisId;

    private Integer reviewId;

    private Integer contentId;

    private String analysisResult;

    private String analysisSesitive;

    private Integer type;

    private Integer similarityDegree;

    private Integer legitimacy;

    private String analysisName;

}