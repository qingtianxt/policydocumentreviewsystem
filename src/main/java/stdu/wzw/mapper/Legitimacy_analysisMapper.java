package stdu.wzw.mapper;

import stdu.wzw.model.Legitimacy_analysis;

import java.util.List;

public interface Legitimacy_analysisMapper {
    int deleteByPrimaryKey(Integer legitimacyId);

    int insert(Legitimacy_analysis record);

    int insertSelective(Legitimacy_analysis record);

    Legitimacy_analysis selectByPrimaryKey(Integer legitimacyId);

    int updateByPrimaryKeySelective(Legitimacy_analysis record);

    int updateByPrimaryKey(Legitimacy_analysis record);

    List<Legitimacy_analysis> getByAnalysisId(Integer analysisId);

    List<Legitimacy_analysis> getAllByReviewId(Integer reviewId);
}