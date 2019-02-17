package stdu.wzw.service;

import stdu.wzw.model.Policy_review_analysis;

import java.util.List;

public interface Policy_review_analysisService {
    void insertList(List<Policy_review_analysis> list);

    Integer getCountByReviewId(Integer reviewId);

    List<Policy_review_analysis> getByReviewId(Integer reviewId);

    List<Policy_review_analysis> getByReviewIdAndType(Integer reviewId, int type);

    void updateList(List<Policy_review_analysis> policy_review_analysisList);

    List<Policy_review_analysis> getBasedByReviewId(Integer reviewId);
}
