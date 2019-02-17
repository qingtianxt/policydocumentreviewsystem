package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.Policy_typeMapper;
import stdu.wzw.model.Policy_type;
import stdu.wzw.service.Policy_typeService;

import java.util.List;

@Service("Policy_typeService")
public class Policy_typeServiceImpl implements Policy_typeService {
    @Autowired
    private Policy_typeMapper policy_typeMapper;


    @Override
    public List<Policy_type> getAll() {
        return policy_typeMapper.getAll();
    }
}
