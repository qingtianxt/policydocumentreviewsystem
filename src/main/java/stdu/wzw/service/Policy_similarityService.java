package stdu.wzw.service;

import stdu.wzw.model.Policy_similarity;

import java.util.List;

public interface Policy_similarityService {
    void insertList(List<Policy_similarity> policy_similarityList);

    List<Policy_similarity> getByAnalysisIdAndType(Integer analysisId);

    List<Policy_similarity> getPolicySimilarity(Integer reviewId);

    List<Policy_similarity> getContentSimilarity(Integer contentId);

    List<Policy_similarity> getContentSimilarityByPolicyId(Integer reviewContentId, Integer policyId);

    List<Policy_similarity> getAnalysisSimilarity(Integer analysisId);

    List<Policy_similarity> getAnalysisSimilarityByPolicyId(Integer analysisId, Integer policyId);
}
