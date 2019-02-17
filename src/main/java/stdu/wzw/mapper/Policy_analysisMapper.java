package stdu.wzw.mapper;

import stdu.wzw.model.Policy_analysis;

import java.util.List;
import java.util.Map;

public interface Policy_analysisMapper {
    int deleteByPrimaryKey(Integer analysisId);

    int insert(Policy_analysis record);

    int insertSelective(Policy_analysis record);

    Policy_analysis selectByPrimaryKey(Integer analysisId);

    int updateByPrimaryKeySelective(Policy_analysis record);

    int updateByPrimaryKeyWithBLOBs(Policy_analysis record);

    int updateByPrimaryKey(Policy_analysis record);

    int getCountByPolicyId(Integer policyId);

    List<Policy_analysis> getByPolicyBased(Integer policyId);

    List<Policy_analysis> findAll();

    List<Policy_analysis> findBytype(int type);

    List<Policy_analysis> getByPolicyAndType(Map<String, Object> map);


}