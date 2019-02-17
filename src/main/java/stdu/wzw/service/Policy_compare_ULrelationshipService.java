package stdu.wzw.service;

import stdu.wzw.model.Policy_compare_ULrelationship;

import java.util.List;

public interface Policy_compare_ULrelationshipService {
    void insertList(List<Policy_compare_ULrelationship> lrelationshipsList);

    List<Policy_compare_ULrelationship> getByReviewId(Integer reviewId);

    List<Policy_compare_ULrelationship> getByReviewIdAndGrade(Integer reviewId, Integer grade);
}
