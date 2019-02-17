package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_compare_ULrelationshipMapper;
import stdu.wzw.model.Policy_compare_ULrelationship;
import stdu.wzw.service.Policy_compare_ULrelationshipService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Policy_compare_ULrelationshipService")
public class Policy_compare_ULrelationshipServiceImpl implements Policy_compare_ULrelationshipService {
    @Autowired
    private Policy_compare_ULrelationshipMapper policy_compare_uLrelationshipMapper;

    @Override
    public void insertList(List<Policy_compare_ULrelationship> lrelationshipsList) {
        for (Policy_compare_ULrelationship p : lrelationshipsList
                ) {
            policy_compare_uLrelationshipMapper.insert(p);
        }
    }

    @Override
    public List<Policy_compare_ULrelationship> getByReviewId(Integer reviewId) {
        return policy_compare_uLrelationshipMapper.getByReviewId(reviewId);
    }

    @Override
    public List<Policy_compare_ULrelationship> getByReviewIdAndGrade(Integer reviewId, Integer grade) {
        Map<String,Object> map =new HashMap<>();
        map.put("reviewId",reviewId);
        map.put("grade",grade);
        return policy_compare_uLrelationshipMapper.getByReviewIdAndGrade(map);
    }
}
