package stdu.wzw.condition;

/**
 * 政策分页条件
 */
public class PolicyCondition {
    private Integer status;//文档状态
    private String start;//开始时间
    private String end;//截止时间
    private String policyName;//政策名称

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    @Override
    public String toString() {
        return "PolicyCondition{" +
                "status=" + status +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", policyName='" + policyName + '\'' +
                '}';
    }
}
