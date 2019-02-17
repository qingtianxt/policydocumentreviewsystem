package stdu.wzw.mapper;

import stdu.wzw.model.Policy_review;

import java.util.List;

public interface Policy_reviewMapper {
    int deleteByPrimaryKey(Integer reviewid);

    int insert(Policy_review record);

    int insertSelective(Policy_review record);

    Policy_review selectByPrimaryKey(Integer reviewid);

    int updateByPrimaryKeySelective(Policy_review record);

    int updateByPrimaryKey(Policy_review record);

    void insertAndGetKey(Policy_review policy_review);

    List<Policy_review> findByType(int type);

    List<Policy_review> findByList();
}