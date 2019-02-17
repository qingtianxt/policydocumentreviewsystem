package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.Policy_review;

import java.util.List;

public interface Policy_reviewService {
    void insertList(List<Policy_review> list);

    PageInfo<Policy_review> findByPage(Integer pageCode, Integer pageSize);

    Policy_review findById(Integer reviewId);

    String insertListAndGetKey(List<Policy_review> list);

    void updateStatus(Policy_review status);

    void update(Policy_review policy_review);

    PageInfo<Policy_review> findByPageAndType(Integer pageCode, Integer pageSize, int type);
}
