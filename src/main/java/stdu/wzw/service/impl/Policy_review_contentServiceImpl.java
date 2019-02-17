package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_review_contentMapper;
import stdu.wzw.model.Policy_review_content;
import stdu.wzw.service.Policy_review_contentService;

import java.util.List;

@Service("Policy_review_contentService")
public class Policy_review_contentServiceImpl implements Policy_review_contentService {
    @Autowired
    private Policy_review_contentMapper policy_review_contentMapper;


    @Override
    public Integer getCount(Integer reviewId) {
        return policy_review_contentMapper.countByReviewId(reviewId);
    }

    @Override
    public List<Policy_review_content> getByReviewId(Integer reviewId) {
        return policy_review_contentMapper.selectByReviewId(reviewId);
    }

    @Override
    public void insertList(List<Policy_review_content> list) {
        for (Policy_review_content content : list
                ) {
            policy_review_contentMapper.insert(content);
        }
    }

    @Override
    public void updateList(List<Policy_review_content> contentList1) {
        for (Policy_review_content p : contentList1
                ) {
            policy_review_contentMapper.updateByPrimaryKey(p);
        }
    }

}

