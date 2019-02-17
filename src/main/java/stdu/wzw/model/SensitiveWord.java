package stdu.wzw.model;

import java.util.Date;

public class SensitiveWord {
    private Integer sensitiveId;

    private String sensitiveName;

    private Integer type;//敏感词分类：1代表关键词 2代表禁用词 3代表常用词

    private Date createDate;

    public Integer getSensitiveId() {
        return sensitiveId;
    }

    public void setSensitiveId(Integer sensitiveId) {
        this.sensitiveId = sensitiveId;
    }

    public String getSensitiveName() {
        return sensitiveName;
    }

    public void setSensitiveName(String sensitiveName) {
        this.sensitiveName = sensitiveName;
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

    @Override
    public String toString() {
        return "SensitiveWord{" +
                "sensitiveId=" + sensitiveId +
                ", sensitiveName='" + sensitiveName + '\'' +
                ", type=" + type +
                ", createDate=" + createDate +
                '}';
    }
}