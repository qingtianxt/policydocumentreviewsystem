package stdu.wzw.model;

import java.util.Date;

public class Policy_analysis {
    private Integer analysisId;

    private Integer type;

    private Date createDate;

    private Integer policyId;

    private Integer contentId;

    private String analysisResult;

    private String analysisSensitive;

    private String analysisName;

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult == null ? null : analysisResult.trim();
    }

    public String getAnalysisSensitive() {
        return analysisSensitive;
    }

    public void setAnalysisSensitive(String analysisSensitive) {
        this.analysisSensitive = analysisSensitive == null ? null : analysisSensitive.trim();
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName == null ? null : analysisName.trim();
    }
}