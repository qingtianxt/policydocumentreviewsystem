package stdu.wzw.model;

import lombok.Data;

import java.util.Date;

@Data
public class ExecuteAndAbolish {
    private Integer executeabolishId;

    private String executeName;

    private String abolishName;

    private String reference;

    private Date createDate;

    private Integer policyId;

    private Date startDate;

    private Date endDate;

}