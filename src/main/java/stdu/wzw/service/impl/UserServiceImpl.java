package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stdu.wzw.mapper.UserMapper;
import stdu.wzw.model.User;
import stdu.wzw.service.UserService;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void save(User user) {
        userMapper.insert(user);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public PageInfo<User> findByPage(Integer pageCode, Integer pageSize, String username) {
        PageHelper.startPage(pageCode, pageSize);
        return new PageInfo<User>(userMapper.findByPage(username));
    }

    @Override
    public void deleteById(Integer userId) {
        userMapper.deleteByPrimaryKey(userId);
    }

    @Override
    public User getById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }
}
