package stdu.wzw.model;

public class Policy_compare_ULrelationship {
    private Integer relationshipId;

    private Integer reviewId;

    private Integer policyId;

    private Integer gradeId;

    private String placeId;

    private Integer type; //1.省 2.市 3.县

    public Integer getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(Integer relationshipId) {
        this.relationshipId = relationshipId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId == null ? null : placeId.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Policy_compare_ULrelationship() {
    }

    public Policy_compare_ULrelationship(Integer reviewId, Integer policyId, Integer gradeId, String placeId, Integer type) {
        this.reviewId = reviewId;
        this.policyId = policyId;
        this.gradeId = gradeId;
        this.placeId = placeId;
        this.type = type;
    }
}