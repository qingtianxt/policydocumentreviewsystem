package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.Policy_content;

import java.util.List;

public interface Policy_contentMapper {
    int deleteByPrimaryKey(Integer contentId);

    int insert(Policy_content record);

    int insertSelective(Policy_content record);

    Policy_content selectByPrimaryKey(Integer contentId);

    int updateByPrimaryKeySelective(Policy_content record);

    int updateByPrimaryKeyWithBLOBs(Policy_content record);

    int updateByPrimaryKey(Policy_content record);

    List<Policy_content> selectByPolicyId(Integer policyId);

    Integer countByPolicyId(Integer policyId);

    void deleteByPolicyId(Integer policyId);

    @Select("select * from policycontent")
    @ResultMap("ResultMapWithBLOBs")
    List<Policy_content> getAll();
}