package stdu.wzw.mapper;

import stdu.wzw.model.Policy;

import java.util.List;
import java.util.Map;

public interface PolicyMapper {
    int deleteByPrimaryKey(Integer policyId);

    int insert(Policy record);

    int insertSelective(Policy record);

    Policy selectByPrimaryKey(Integer policyId);

    int updateByPrimaryKeySelective(Policy record);

    int updateByPrimaryKey(Policy record);

    List<Policy> findAll();

    void insertAndGetKey(Policy policy);

    List<Policy> getByPolicyType(Integer policyType);

    List<Policy> findByLikePolicyName(String policyName);

    Policy findByPolicyName(String policyName);

    List<Policy> findBylist(Map<String, Object> map);

    List<Policy> getByGrade(Integer gradeId);

    List<Policy> getByPolicyTypeAndGrade(Map<String, Object> map);
}