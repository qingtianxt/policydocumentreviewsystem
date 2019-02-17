package stdu.wzw.service;

import stdu.wzw.model.Policy_content;

import java.util.List;

public interface Policy_contentService {
    void insertList(List<Policy_content>list);

    List<Policy_content> getById(Integer policyId);

    Integer getCount(Integer policyId);

    Policy_content getByContentId(Integer contentId);

    List<Policy_content> getAll();
}
