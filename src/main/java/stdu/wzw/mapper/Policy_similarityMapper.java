package stdu.wzw.mapper;

import stdu.wzw.model.Policy_similarity;

import java.util.List;
import java.util.Map;

public interface Policy_similarityMapper {
    int deleteByPrimaryKey(Integer similarityId);

    int insert(Policy_similarity record);

    int insertSelective(Policy_similarity record);

    Policy_similarity selectByPrimaryKey(Integer similarityId);

    int updateByPrimaryKeySelective(Policy_similarity record);

    int updateByPrimaryKey(Policy_similarity record);

    List<Policy_similarity> getByAnalysisIdAndType(Map<String, Object> map);

    List<Policy_similarity> getPolicySimilarity(Map<String, Object> map);

    List<Policy_similarity> getContentSimilarity(Map<String, Object> map);

    List<Policy_similarity> getContentSimilarityByReviewContentId(Map<String, Object> map);

    List<Policy_similarity> getContentSimilarityByPolicyId(Map<String, Object> map);

    List<Policy_similarity> getAnalysisSimilarity(Map<String, Object> map);

    List<Policy_similarity> getAnalysisSimilarityByPolicyId(Map<String, Object> map);
}