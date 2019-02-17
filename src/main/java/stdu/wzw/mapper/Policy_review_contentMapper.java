package stdu.wzw.mapper;

import stdu.wzw.model.Policy_review_content;

import java.util.List;

public interface Policy_review_contentMapper {
    int deleteByPrimaryKey(Integer contentId);

    int insert(Policy_review_content record);

    int insertSelective(Policy_review_content record);

    Policy_review_content selectByPrimaryKey(Integer contentId);

    int updateByPrimaryKeySelective(Policy_review_content record);

    int updateByPrimaryKeyWithBLOBs(Policy_review_content record);

    int updateByPrimaryKey(Policy_review_content record);

    List<Policy_review_content> selectByReviewId(Integer reviewId);

    Integer countByReviewId(Integer reviewId);
}