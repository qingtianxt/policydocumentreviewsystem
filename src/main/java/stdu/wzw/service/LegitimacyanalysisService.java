package stdu.wzw.service;

import stdu.wzw.model.Legitimacy_analysis;

import java.util.List;

public interface LegitimacyanalysisService {
    void insertList(List<Legitimacy_analysis> list);

    List<Legitimacy_analysis> getByAnalysisId(Integer analysisId);

    List<Legitimacy_analysis> getAllByReviewId(Integer reviewId);
}
