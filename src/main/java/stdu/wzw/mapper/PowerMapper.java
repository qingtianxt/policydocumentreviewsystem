package stdu.wzw.mapper;

import stdu.wzw.model.Power;

import java.util.List;

public interface PowerMapper {
    int deleteByPrimaryKey(Integer powerId);

    int insert(Power record);

    int insertSelective(Power record);

    Power selectByPrimaryKey(Integer powerId);

    int updateByPrimaryKeySelective(Power record);

    int updateByPrimaryKey(Power record);

    List<Power> findAll();
}