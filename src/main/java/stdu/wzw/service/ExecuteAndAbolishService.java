package stdu.wzw.service;

import stdu.wzw.model.ExecuteAndAbolish;

import java.util.List;

public interface ExecuteAndAbolishService {
    void insertList(List<ExecuteAndAbolish> executeAndAbolishList);

    List<ExecuteAndAbolish> findByPolicyId(Integer policyId);
}
