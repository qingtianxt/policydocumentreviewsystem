package stdu.wzw.mapper;

import stdu.wzw.model.ExecuteAndAbolish;

import java.util.List;

public interface ExecuteAndAbolishMapper {
    int deleteByPrimaryKey(Integer executeabolishId);

    int insert(ExecuteAndAbolish record);

    int insertSelective(ExecuteAndAbolish record);

    ExecuteAndAbolish selectByPrimaryKey(Integer executeabolishId);

    int updateByPrimaryKeySelective(ExecuteAndAbolish record);

    int updateByPrimaryKey(ExecuteAndAbolish record);

    List<ExecuteAndAbolish> findByPolicyId(Integer policyId);
}