package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.Policy_analysis;

import java.util.List;

public interface Policy_analysisService {
    int getCountByPolicyId(Integer policyId);

    void insertList(List<Policy_analysis> list);

    List<Policy_analysis> getByPolicyBased(Integer policyId);

    PageInfo<Policy_analysis> findSentenceByPage(Integer pageCode, Integer pageSize, Integer policyId);

    List<Policy_analysis> findAll();

    List<Policy_analysis> findBytype(int type);

    Policy_analysis getById(Integer analysisId);

    List<Policy_analysis> getByPolicyAndType(Integer policyId, int i);
}
