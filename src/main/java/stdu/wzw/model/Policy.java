package stdu.wzw.model;

import lombok.Data;

import java.util.Date;

@Data
public class Policy {
    private Integer policyId;

    private String policyName;

    private String url;

    private Date uploadDate;

    private Integer status;

    private Integer userId;

    private Integer policyType;

    private Date startDate;

    private Date endDate;

    private Integer policyGrade;

    private Integer precursor;

    private Integer successor;

}