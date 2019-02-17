package stdu.wzw.model;

public class Legitimacy_analysis {
    private Integer legitimacyId;

    private Integer analysisId;

    private Integer type;

    private String info;

    private Integer reviewId;

    private Integer legitimacy;

    public Integer getLegitimacyId() {
        return legitimacyId;
    }

    public void setLegitimacyId(Integer legitimacyId) {
        this.legitimacyId = legitimacyId;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getLegitimacy() {
        return legitimacy;
    }

    public void setLegitimacy(Integer legitimacy) {
        this.legitimacy = legitimacy;
    }

    public Legitimacy_analysis(Integer analysisId, Integer type, String info, Integer reviewId, Integer legitimacy) {
        this.analysisId = analysisId;
        this.type = type;
        this.info = info;
        this.reviewId = reviewId;
        this.legitimacy = legitimacy;
    }

    public Legitimacy_analysis(Integer type, String info, Integer reviewId, Integer legitimacy) {
        this.type = type;
        this.info = info;
        this.reviewId = reviewId;
        this.legitimacy = legitimacy;
    }

    public Legitimacy_analysis() {
    }
}