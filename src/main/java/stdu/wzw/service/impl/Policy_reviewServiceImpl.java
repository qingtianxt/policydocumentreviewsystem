package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_reviewMapper;
import stdu.wzw.model.Policy_review;
import stdu.wzw.service.Policy_reviewService;

import java.util.List;

@Service
public class Policy_reviewServiceImpl implements Policy_reviewService {

    @Autowired
    private Policy_reviewMapper policy_reviewMapper;

    @Override
    public void insertList(List<Policy_review> list) {
        for (Policy_review p : list
                ) {
            policy_reviewMapper.insertSelective(p);
        }
    }

    @Override
    public PageInfo<Policy_review> findByPage(Integer pageCode, Integer pageSize) {
        PageHelper.startPage(pageCode, pageSize);
        List<Policy_review> list =policy_reviewMapper.findByList();
        return new PageInfo<>(list);
    }

    @Override
    public Policy_review findById(Integer reviewId) {
        return policy_reviewMapper.selectByPrimaryKey(reviewId);
    }

    /**
     * 添加多条数据，并能获取自增的审查文件id
     *
     * @param list
     * @return
     */
    @Override
    public String insertListAndGetKey(List<Policy_review> list) {
        String keys = "";
        int i = 0;
        for (Policy_review policy_review : list
                ) {
            policy_reviewMapper.insertAndGetKey(policy_review);
            if (i == 0) {
                keys += policy_review.getReviewid();
                i++;
            } else {
                keys += "," + policy_review.getReviewid();
            }
        }
        return keys;
    }

    /**
     * 用于更新状态
     *
     * @param status
     */
    @Override
    public void updateStatus(Policy_review status) {
        policy_reviewMapper.updateByPrimaryKey(status);
    }

    /**
     * 更新审查文章信息的通用方法
     *
     * @param policy_review
     */
    @Override
    public void update(Policy_review policy_review) {
        policy_reviewMapper.updateByPrimaryKey(policy_review);
    }

    @Override
    public PageInfo<Policy_review> findByPageAndType(Integer pageCode, Integer pageSize, int type) {
        PageHelper.startPage(pageCode, pageSize);
        List<Policy_review> list = policy_reviewMapper.findByType(type);
        return new PageInfo<>(list);
    }

}
