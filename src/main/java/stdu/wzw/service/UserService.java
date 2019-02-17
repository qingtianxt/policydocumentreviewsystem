package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.User;

public interface UserService {
    void save(User user);

    User getByUsername(String username);

    PageInfo<User> findByPage(Integer pageCode, Integer pageSize, String username);

    void deleteById(Integer userId);

    User getById(Integer userId);
}
