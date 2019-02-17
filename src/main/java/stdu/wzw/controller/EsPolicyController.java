package stdu.wzw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.EsPolicy;
import stdu.wzw.model.Policy;
import stdu.wzw.model.Policy_analysis;
import stdu.wzw.repository.EsPolicyRepository;
import stdu.wzw.service.PolicyService;
import stdu.wzw.service.Policy_analysisService;
import stdu.wzw.utils.StringUtil;

import java.util.List;


@RestController
@RequestMapping("/EsPolicy")
public class EsPolicyController {

    @Autowired
    private EsPolicyRepository esPolicyRepository;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private Policy_analysisService policy_analysisService;

    @RequestMapping("/findByPage")
    public ModelAndView findByPage(@RequestParam(value = "pageCode", defaultValue = "0") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, String content) {


        ModelAndView mv = new ModelAndView("/relationship/search_results");
        if (null != content) {
            Pageable pageable = new PageRequest(pageCode, pageSize);
            Page<EsPolicy> page = esPolicyRepository.findByPolicyNameLikeOrAnalysisNameLikeOrSummaryLike(content, content, content, pageable);
            mv.addObject("page", page);
            mv.addObject("content", content);
            if (null != page && page.getContent().size() > 0) {
                mv.addObject("status", 1);
            }
        }
        return mv;
    }

    @RequestMapping("/insertAnalysis")
    public String insert() {
        esPolicyRepository.deleteAll();
        List<Policy> policyList = policyService.findAll();

        for (Policy policy : policyList
                ) {
            int i = 0;
            List<Policy_analysis> analysisList = policy_analysisService.getByPolicyBased(policy.getPolicyId());
            for (Policy_analysis analysis : analysisList
                    ) {
                String randomString = StringUtil.getRandomString(20);
                EsPolicy es = new EsPolicy(randomString, policy.getPolicyId(), policy.getPolicyName(), analysis.getAnalysisId(), analysis.getAnalysisName(), analysis.getAnalysisResult());
                esPolicyRepository.save(es);
                if (i == 0) {
                    System.out.println("开始添加");
                    i++;
                }
            }
        }
        System.out.println("添加索引完成");
        //  esPolicyRepository.save(new EsPolicy("2", "老卫和你一起学 Elasticsearch", "如何来学习 Elasticsearch", "最终看我的博客 https://waylau.com，酒", 2, 3));

        return "success";
    }

    @RequestMapping("/deleteAll")
    public String deleteAll() {
        esPolicyRepository.deleteAll();
        return "success";
    }
}
