package stdu.wzw.mapper;

import stdu.wzw.model.Policy_review_analysis;

import java.util.List;
import java.util.Map;

public interface Policy_review_analysisMapper {
    int deleteByPrimaryKey(Integer analysisId);

    int insert(Policy_review_analysis record);

    int insertSelective(Policy_review_analysis record);

    Policy_review_analysis selectByPrimaryKey(Integer analysisId);

    int updateByPrimaryKeySelective(Policy_review_analysis record);

    int updateByPrimaryKeyWithBLOBs(Policy_review_analysis record);

    int updateByPrimaryKey(Policy_review_analysis record);

    Integer getCountByReviewId(Integer reviewId);

    List<Policy_review_analysis> getByReviewId(Integer reviewId);

    List<Policy_review_analysis> getByReviewIdAndType(Map<String, Object> map);

    List<Policy_review_analysis> getBasedByReviewId(Integer reviewId);

}