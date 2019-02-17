package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.condition.PolicyCondition;
import stdu.wzw.mapper.PolicyMapper;
import stdu.wzw.mapper.Policy_contentMapper;
import stdu.wzw.model.Policy;
import stdu.wzw.service.PolicyService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("policyService")
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private Policy_contentMapper policy_contentMapper;

    private static Logger logger = Logger.getLogger(PolicyServiceImpl.class);

    @Override
    public void insertMany(List<Policy> list) {

        for (Policy policy : list
                ) {
            policyMapper.insert(policy);
        }
    }

    @Override
    public PageInfo<Policy> findByPage(Integer pageCode, Integer pageSize, PolicyCondition policyCondition) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("policyName", policyCondition.getPolicyName());
        map.put("status", policyCondition.getStatus());
        map.put("start", policyCondition.getStart());
        map.put("end", policyCondition.getEnd());
        logger.info(map);
        PageHelper.startPage(pageCode, pageSize);
        List<Policy> list = policyMapper.findBylist(map);

      /*  logger.info("-----------------policyList----------------");
        logger.info(list);
        logger.info("-------------------------------------------");*/
        return new PageInfo<>(list);
    }

    /**
     * 根据id查找政策
     *
     * @param policyId
     * @return
     */
    @Override
    public Policy findById(Integer policyId) {
        return policyMapper.selectByPrimaryKey(policyId);
    }

    /**
     * 根据id删除政策文档
     *
     * @param policyId
     */
    @Override
    public void deleteById(Integer policyId) {
        Policy policy = policyMapper.selectByPrimaryKey(policyId);
        //删除政策文件夹中的文件
        if (null != policy.getUrl() && !"".equals(policy.getUrl())) {
            File file = new File(policy.getUrl());
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }

        //删除政策内容中的数据

        policy_contentMapper.deleteByPolicyId(policyId);
        policyMapper.deleteByPrimaryKey(policyId);
    }

    @Override
    public void updateStatus(Integer policyId, int i) {
        Policy p = new Policy();
        p.setStatus(i);
        p.setPolicyId(policyId);
        policyMapper.updateByPrimaryKeySelective(p);
    }

    @Override
    public List<Policy> findAll() {
        return policyMapper.findAll();
    }

    @Override
    public String insertListAndGetKey(List<Policy> list) {
        String keys = "";
        int i = 0;
        for (Policy policy : list
                ) {
            policyMapper.insertAndGetKey(policy);
            if (i == 0) {
                keys += policy.getPolicyId();
                i++;
            } else {
                keys += "," + policy.getPolicyId();
            }
        }
        return keys;
    }

    @Override
    public void update(Policy p) {
        policyMapper.updateByPrimaryKey(p);
    }

    @Override
    public List<Policy> getByPolicyType(Integer policyType) {
        return policyMapper.getByPolicyType(policyType);
    }

    @Override
    public List<Policy> findByLikePolicyName(String policyName) {
        policyName = "%" + policyName + "%";
        return policyMapper.findByLikePolicyName(policyName);
    }

    @Override
    public Policy findByPolicyName(String policyName) {
        return policyMapper.findByPolicyName(policyName);
    }

    @Override
    public List<Policy> getByGrade(Integer gradeId) {
        return policyMapper.getByGrade(gradeId);
    }

    @Override
    public List<Policy> getByPolicyTypeAndGrade(Integer policyType, int grade) {
        Map<String, Object> map = new HashMap<>();
        map.put("policyType", policyType);
        map.put("gradeId", grade);
        return policyMapper.getByPolicyTypeAndGrade(map);
    }
}
