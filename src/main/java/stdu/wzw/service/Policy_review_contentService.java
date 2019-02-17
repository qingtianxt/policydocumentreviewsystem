package stdu.wzw.service;

import stdu.wzw.model.Policy_review_content;

import java.util.List;

public interface Policy_review_contentService {
    Integer getCount(Integer reviewId);

    List<Policy_review_content> getByReviewId(Integer reviewId);

    void insertList(List<Policy_review_content>list);

    void updateList(List<Policy_review_content> contentList1);
}
