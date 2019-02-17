package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.condition.PolicyCondition;
import stdu.wzw.model.Policy;

import java.util.List;

public interface PolicyService {

    void insertMany(List<Policy> list);

    PageInfo<Policy> findByPage(Integer pageCode, Integer pageSize, PolicyCondition policyCondition);

    Policy findById(Integer policyId);

    void deleteById(Integer policyId);

    void updateStatus(Integer policyId, int i);

    List<Policy> findAll();

    String insertListAndGetKey(List<Policy> list);

    void update(Policy p);

    List<Policy> getByPolicyType(Integer policyType);

    List<Policy> findByLikePolicyName(String policyName);

    Policy findByPolicyName(String qianqu);

    List<Policy> getByGrade(Integer gradeId);

    List<Policy> getByPolicyTypeAndGrade(Integer policyType, int grade);
}
