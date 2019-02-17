package stdu.wzw.condition;

import lombok.Data;
import stdu.wzw.model.Policy;

import java.util.List;

/**
 * 辅助对比，增加省份id信息
 */
@Data
public class Policy_compareCondition {
    private List<Policy> policyList;
    private String placeId;

}
