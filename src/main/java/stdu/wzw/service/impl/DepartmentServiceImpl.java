package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.DepartmentMapper;
import stdu.wzw.model.Department;
import stdu.wzw.service.DepartmentService;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> getByParentId(Integer parentId) {
        return departmentMapper.selectByParentId(parentId);
    }

    @Override
    public Department getByDepartmentId(Integer departmentId) {
        return departmentMapper.selectByPrimaryKey(departmentId);
    }

    @Override
    public List<Department> getAll() {
        return departmentMapper.getAll();
    }

    @Override
    public void save(Department department) {
        departmentMapper.insert(department);
    }

    @Override
    public PageInfo<Department> findByPage(Integer pageCode, Integer pageSize) {
        PageHelper.startPage(pageCode, pageSize);
        List<Department> list = departmentMapper.getAll();
        return new PageInfo<>(list);
    }
}
