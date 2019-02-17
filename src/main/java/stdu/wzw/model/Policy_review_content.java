package stdu.wzw.model;

public class Policy_review_content {
    private Integer contentId;

    private Integer reviewId;

    private Integer location;

    private Integer similarityDegree;

    private String contentText;

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getSimilarityDegree() {
        return similarityDegree;
    }

    public void setSimilarityDegree(Integer similarityDegree) {
        this.similarityDegree = similarityDegree;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText == null ? null : contentText.trim();
    }

    @Override
    public String toString() {
        return "Policy_review_content{" +
                "contentId=" + contentId +
                ", reviewId=" + reviewId +
                ", location=" + location +
                ", similarityDegree=" + similarityDegree +
                ", contentText='" + contentText + '\'' +
                '}';
    }
}