package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_similarityMapper;
import stdu.wzw.model.Policy_similarity;
import stdu.wzw.service.Policy_similarityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Policy_similarityService")
public class Policy_similarityServiceImpl implements Policy_similarityService {
    @Autowired
    private Policy_similarityMapper policy_similarityMapper;

    @Override
    public void insertList(List<Policy_similarity> policy_similarityList) {
        for (Policy_similarity p : policy_similarityList
                ) {
            policy_similarityMapper.insert(p);
        }
    }


    @Override
    public List<Policy_similarity> getByAnalysisIdAndType(Integer analysisId) {
        Map<String, Object> map = new HashMap<>();
        map.put("analysisId", analysisId);
        map.put("type", 1);

        return policy_similarityMapper.getByAnalysisIdAndType(map);
    }

    @Override
    public List<Policy_similarity> getPolicySimilarity(Integer reviewId) {
        Map<String, Object> map = new HashMap<>();
        map.put("reviewId", reviewId);
        map.put("type", 2);
        return policy_similarityMapper.getPolicySimilarity(map);
    }

    @Override
    public List<Policy_similarity> getContentSimilarity(Integer contentId) {
        Map<String, Object> map = new HashMap<>();
        map.put("contentId", contentId);
        map.put("type", 3);
        return policy_similarityMapper.getContentSimilarity(map);
    }

    @Override
    public List<Policy_similarity> getContentSimilarityByPolicyId(Integer reviewContentId, Integer policyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("reviewContentId", reviewContentId);
        map.put("type", 3);
        map.put("policyId", policyId);
        return policy_similarityMapper.getContentSimilarityByPolicyId(map);
    }

    @Override
    public List<Policy_similarity> getAnalysisSimilarity(Integer analysisId) {
        Map<String, Object> map = new HashMap<>();
        map.put("analysisId", analysisId);
        map.put("type", 1);
        return policy_similarityMapper.getAnalysisSimilarity(map);
    }

    @Override
    public List<Policy_similarity> getAnalysisSimilarityByPolicyId(Integer analysisId, Integer policyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("analysisId", analysisId);
        map.put("type", 1);
        map.put("policyId", policyId);
        return policy_similarityMapper.getAnalysisSimilarityByPolicyId(map);
    }

}
