package stdu.wzw.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import stdu.wzw.model.EsPolicy;

import java.util.List;

public interface EsPolicyRepository extends ElasticsearchRepository<EsPolicy, String> {

    // Page<EsPolicy> findByTitleLikeOrContentLikeOrSummaryLike(String title,String content,String summary, Pageable pageable);
    Page<EsPolicy> findByPolicyNameLikeOrAnalysisNameLikeOrSummaryLike(String policyName, String analysisName, String summary, Pageable pageable);

    Page<EsPolicy> findByPolicyNameLike(String content, Pageable pageable);

    List<EsPolicy> findByAnalysisNameLikeAndPolicyIdEquals(String analysisName, Integer policyId);
}
