package stdu.wzw.mapper;

import stdu.wzw.model.Policy_compare_ULrelationship;

import java.util.List;
import java.util.Map;

public interface Policy_compare_ULrelationshipMapper {
    int deleteByPrimaryKey(Integer relationshipId);

    int insert(Policy_compare_ULrelationship record);

    int insertSelective(Policy_compare_ULrelationship record);

    Policy_compare_ULrelationship selectByPrimaryKey(Integer relationshipId);

    int updateByPrimaryKeySelective(Policy_compare_ULrelationship record);

    int updateByPrimaryKey(Policy_compare_ULrelationship record);

    List<Policy_compare_ULrelationship> getByReviewId(Integer reviewId);

    List<Policy_compare_ULrelationship> getByReviewIdAndGrade(Map<String, Object> map);
}