package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_contentMapper;
import stdu.wzw.model.Policy_content;
import stdu.wzw.service.Policy_contentService;

import java.util.List;

@Service("Policy_contentService")
public class Policy_contentServiceImpl implements Policy_contentService {

    @Autowired
    private Policy_contentMapper policy_contentMapper;

    /**
     * 插入多条政策分段内容
     *
     * @param list
     */
    @Override
    public void insertList(List<Policy_content> list) {
        for (Policy_content content : list
                ) {
            policy_contentMapper.insert(content);
        }
    }

    /**
     * 根据政策id查看政策内容
     *
     * @param policyId
     * @return
     */
    @Override
    public List<Policy_content> getById(Integer policyId) {
        try {
            List<Policy_content> list = policy_contentMapper.selectByPolicyId(policyId);
            return list;
        } catch (NullPointerException e) {
            return null;
        }


    }

    @Override
    public Integer getCount(Integer policyId) {
        return policy_contentMapper.countByPolicyId(policyId);
    }

    @Override
    public Policy_content getByContentId(Integer contentId) {
        return policy_contentMapper.selectByPrimaryKey(contentId);
    }

    @Override
    public List<Policy_content> getAll() {
        return policy_contentMapper.getAll();
    }
}
