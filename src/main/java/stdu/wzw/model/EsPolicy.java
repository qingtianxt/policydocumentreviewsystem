package stdu.wzw.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * policy带索引
 */
@Data
@Document(indexName = "policy", type = "policy")
public class EsPolicy implements Serializable {
    @Id
    private String id;
    private Integer policyId;
    private String policyName;
    private Integer analysisId;
    private String analysisName;
    private String summary;//此处的摘要选择的是段落的。


    protected EsPolicy() {

    }

    public EsPolicy(String id, Integer policyId, String policyName, Integer analysisId, String analysisName, String summary) {
        this.id = id;
        this.policyId = policyId;
        this.policyName = policyName;
        this.analysisId = analysisId;
        this.analysisName = analysisName;
        this.summary = summary;
    }
}
