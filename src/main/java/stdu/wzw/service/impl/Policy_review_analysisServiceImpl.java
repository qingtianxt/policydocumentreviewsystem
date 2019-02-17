package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_review_analysisMapper;
import stdu.wzw.model.Policy_review_analysis;
import stdu.wzw.service.Policy_review_analysisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Policy_review_analysisService")
public class Policy_review_analysisServiceImpl implements Policy_review_analysisService {
    @Autowired
    private Policy_review_analysisMapper policy_review_analysisMapper;


    @Override
    public void insertList(List<Policy_review_analysis> list) {
        for (Policy_review_analysis p : list
                ) {
            policy_review_analysisMapper.insert(p);
        }
    }

    @Override
    public Integer getCountByReviewId(Integer reviewId) {
        return policy_review_analysisMapper.getCountByReviewId(reviewId);
    }

    @Override
    public List<Policy_review_analysis> getByReviewId(Integer reviewId) {
        return policy_review_analysisMapper.getByReviewId(reviewId);
    }

    @Override
    public List<Policy_review_analysis> getByReviewIdAndType(Integer reviewId, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("reviewId", reviewId);
        map.put("type", type);
        return policy_review_analysisMapper.getByReviewIdAndType(map);
    }

    @Override
    public void updateList(List<Policy_review_analysis> policy_review_analysisList) {
        for (Policy_review_analysis p : policy_review_analysisList
                ) {
            policy_review_analysisMapper.updateByPrimaryKey(p);
        }
    }

    /**
     * 获取除去核心精神外的所有句子信息
     *
     * @param reviewId
     * @return
     */
    @Override
    public List<Policy_review_analysis> getBasedByReviewId(Integer reviewId) {

        return policy_review_analysisMapper.getBasedByReviewId(reviewId);
    }
}
