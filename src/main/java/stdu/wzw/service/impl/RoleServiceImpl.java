package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.RoleMapper;
import stdu.wzw.model.Role;
import stdu.wzw.service.RoleService;

import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public PageInfo<Role> findByPage(Integer pageCode, Integer pageSize) {
        PageHelper.startPage(pageCode, pageSize);
        return new PageInfo<Role>(roleMapper.findAll());
    }

    @Override
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public Role findByRoleId(Integer roleId) {
        return roleMapper.selectByPrimaryKey(roleId);
    }

    @Override
    public void delete(Integer roleId) {
        roleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

}
