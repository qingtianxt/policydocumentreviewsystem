package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_analysisMapper;
import stdu.wzw.model.Policy_analysis;
import stdu.wzw.service.Policy_analysisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Policy_analysisService")
public class Policy_analysisServiceImpl implements Policy_analysisService {
    @Autowired
    private Policy_analysisMapper policy_analysisMapper;
    private static Logger logger = Logger.getLogger(PolicyServiceImpl.class);

    /**
     * 根据政策id获取政策的信息总数
     *
     * @param policyId
     * @return
     */
    @Override
    public int getCountByPolicyId(Integer policyId) {
        return policy_analysisMapper.getCountByPolicyId(policyId);
    }

    /**
     * 插入多条数据
     *
     * @param list
     */
    @Override
    public void insertList(List<Policy_analysis> list) {
        for (Policy_analysis p : list
                ) {
            policy_analysisMapper.insert(p);
        }
    }

    @Override
    public List<Policy_analysis> getByPolicyBased(Integer policyId) {
        return policy_analysisMapper.getByPolicyBased(policyId);
    }

    @Override
    public List<Policy_analysis> getByPolicyAndType(Integer policyId, int i) {
        Map<String, Object> map = new HashMap<>();
        map.put("policyId", policyId);
        map.put("type", i);
        return policy_analysisMapper.getByPolicyAndType(map);
    }


    @Override
    public PageInfo<Policy_analysis> findSentenceByPage(Integer pageCode, Integer pageSize, Integer policyId) {
        PageHelper.startPage(pageCode, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("policyId", policyId);
        map.put("type", 9);
        List<Policy_analysis> list = policy_analysisMapper.getByPolicyAndType(map);
        /*logger.info("-----------------policyList----------------");
        logger.info(list);
        logger.info("-------------------------------------------");*/
        return new PageInfo<>(list);
    }

    @Override
    public List<Policy_analysis> findAll() {
        return policy_analysisMapper.findAll();
    }

    @Override
    public List<Policy_analysis> findBytype(int type) {
        return policy_analysisMapper.findBytype(type);
    }

    @Override
    public Policy_analysis getById(Integer analysisId) {
        return policy_analysisMapper.selectByPrimaryKey(analysisId);
    }

}
