package stdu.wzw.model;

public class Policy_similarity {
    private Integer similarityId;

    private Integer similarityDegree;

    private Integer similarityPolicyId;

    private Integer similarityContentId;

    private Integer similarityAnalysisId;

    private Integer reviewId;

    private Integer reviewContentId;

    private Integer reviewAnanlysisId;

    private Integer type;

    public Integer getSimilarityId() {
        return similarityId;
    }

    public void setSimilarityId(Integer similarityId) {
        this.similarityId = similarityId;
    }

    public Integer getSimilarityDegree() {
        return similarityDegree;
    }

    public void setSimilarityDegree(Integer similarityDegree) {
        this.similarityDegree = similarityDegree;
    }

    public Integer getSimilarityPolicyId() {
        return similarityPolicyId;
    }

    public void setSimilarityPolicyId(Integer similarityPolicyId) {
        this.similarityPolicyId = similarityPolicyId;
    }

    public Integer getSimilarityContentId() {
        return similarityContentId;
    }

    public void setSimilarityContentId(Integer similarityContentId) {
        this.similarityContentId = similarityContentId;
    }

    public Integer getSimilarityAnalysisId() {
        return similarityAnalysisId;
    }

    public void setSimilarityAnalysisId(Integer similarityAnalysisId) {
        this.similarityAnalysisId = similarityAnalysisId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getReviewContentId() {
        return reviewContentId;
    }

    public void setReviewContentId(Integer reviewContentId) {
        this.reviewContentId = reviewContentId;
    }

    public Integer getReviewAnanlysisId() {
        return reviewAnanlysisId;
    }

    public void setReviewAnanlysisId(Integer reviewAnanlysisId) {
        this.reviewAnanlysisId = reviewAnanlysisId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Policy_similarity(Integer similarityId, Integer similarityDegree) {
        this.similarityId = similarityId;
        this.similarityDegree = similarityDegree;
    }

    public Policy_similarity() {
    }

    @Override
    public String toString() {
        return "Policy_similarity{" +
                "similarityId=" + similarityId +
                ", similarityDegree=" + similarityDegree +
                ", similarityPolicyId=" + similarityPolicyId +
                ", similarityContentId=" + similarityContentId +
                ", similarityAnalysisId=" + similarityAnalysisId +
                ", reviewId=" + reviewId +
                ", reviewContentId=" + reviewContentId +
                ", reviewAnanlysisId=" + reviewAnanlysisId +
                ", type=" + type +
                '}';
    }
}