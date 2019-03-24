package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.Role;

import java.util.List;

public interface RoleService {

    PageInfo<Role> findByPage(Integer pageCode, Integer pageSize);

    void add(Role role);

    Role findByRoleId(Integer roleId);

    void delete(Integer roleId);

    List<Role> findAll();
}
