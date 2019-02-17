package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.Policy_type;

import java.util.List;

public interface Policy_typeMapper {
    int deleteByPrimaryKey(Integer typeId);

    int insert(Policy_type record);

    int insertSelective(Policy_type record);

    Policy_type selectByPrimaryKey(Integer typeId);

    int updateByPrimaryKeySelective(Policy_type record);

    int updateByPrimaryKey(Policy_type record);

    @Select("select * from policytype")
    @ResultMap("BaseResultMap")
    List<Policy_type> getAll();
}