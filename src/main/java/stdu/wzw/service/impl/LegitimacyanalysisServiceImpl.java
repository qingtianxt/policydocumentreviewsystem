package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Legitimacy_analysisMapper;
import stdu.wzw.model.Legitimacy_analysis;
import stdu.wzw.service.LegitimacyanalysisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("LegitimacyanalysisService")
public class LegitimacyanalysisServiceImpl implements LegitimacyanalysisService {
    @Autowired
    private Legitimacy_analysisMapper legitimacy_analysisMapper;

    @Override
    public void insertList(List<Legitimacy_analysis> list) {
        for (Legitimacy_analysis legitimacy_analysis : list
                ) {
            legitimacy_analysisMapper.insert(legitimacy_analysis);
        }
    }

    @Override
    public List<Legitimacy_analysis> getByAnalysisId(Integer analysisId) {
       /* Map<String, Object> map = new HashMap<>();
        map.put("analysisId", analysisId);*/
        return legitimacy_analysisMapper.getByAnalysisId(analysisId);
    }

    @Override
    public List<Legitimacy_analysis> getAllByReviewId(Integer reviewId) {
        return legitimacy_analysisMapper.getAllByReviewId(reviewId);
    }
}
