package stdu.wzw.model;

import lombok.Data;

/**
 * 为对比报告提供数据
 */
@Data
public class CompareForWord {
    private Integer policyId;
    private String upload_date;
    private String policyName;

    private String userName;
    private String departmentName;
    private Integer maxSimilarity;

    private String ifAbolish;

    public CompareForWord() {
    }

    public CompareForWord(Integer policyId, String upload_date, String policyName, String userName, String departmentName, Integer maxSimilarity) {
        this.policyId = policyId;
        this.upload_date = upload_date;
        this.policyName = policyName;
        this.userName = userName;
        this.departmentName = departmentName;
        this.maxSimilarity = maxSimilarity;
    }

    public CompareForWord(String upload_date, String policyName, String userName, String departmentName, String ifAbolish) {
        this.upload_date = upload_date;
        this.policyName = policyName;
        this.userName = userName;
        this.departmentName = departmentName;
        this.ifAbolish = ifAbolish;
    }

    //此方法在调用时maxSimilarity存储的是相似度，不是最大相似度。
    public CompareForWord(String upload_date, String policyName, String userName, String departmentName, Integer maxSimilarity, String ifAbolish) {
        this.upload_date = upload_date;
        this.policyName = policyName;
        this.userName = userName;
        this.departmentName = departmentName;
        this.maxSimilarity = maxSimilarity;
        this.ifAbolish = ifAbolish;
    }
}
