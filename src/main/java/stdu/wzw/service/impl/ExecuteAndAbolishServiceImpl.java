package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.ExecuteAndAbolishMapper;
import stdu.wzw.model.ExecuteAndAbolish;
import stdu.wzw.service.ExecuteAndAbolishService;

import java.util.List;

@Service("ExecuteAndAbolishService")
public class ExecuteAndAbolishServiceImpl implements ExecuteAndAbolishService {

    @Autowired
    private ExecuteAndAbolishMapper executeAndAbolishMapper;

    @Override
    public void insertList(List<ExecuteAndAbolish> executeAndAbolishList) {
        for (ExecuteAndAbolish executeAndAbolish : executeAndAbolishList
                ) {
            executeAndAbolishMapper.insert(executeAndAbolish);
        }
    }

    @Override
    public List<ExecuteAndAbolish> findByPolicyId(Integer policyId) {
        return executeAndAbolishMapper.findByPolicyId(policyId);
    }
}
