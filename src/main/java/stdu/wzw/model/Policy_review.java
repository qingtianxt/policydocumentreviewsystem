package stdu.wzw.model;

import java.util.Date;

public class Policy_review {
    private Integer reviewid;

    private String reviewName;

    private String url;

    private Integer type;

    private Date uploadDate;

    private Integer status;

    private Integer departmentId;

    private Integer userId;

    private Integer similarityDegree;

    private Integer legitimacydegree;

    private Integer grade;

    public Integer getReviewid() {
        return reviewid;
    }

    public void setReviewid(Integer reviewid) {
        this.reviewid = reviewid;
    }

    public String getReviewName() {
        return reviewName;
    }

    public void setReviewName(String reviewName) {
        this.reviewName = reviewName == null ? null : reviewName.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSimilarityDegree() {
        return similarityDegree;
    }

    public void setSimilarityDegree(Integer similarityDegree) {
        this.similarityDegree = similarityDegree;
    }

    public Integer getLegitimacydegree() {
        return legitimacydegree;
    }

    public void setLegitimacydegree(Integer legitimacydegree) {
        this.legitimacydegree = legitimacydegree;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}