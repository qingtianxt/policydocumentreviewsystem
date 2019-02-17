package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getByParentId(Integer parentId);

    Department getByDepartmentId(Integer departmentId);

    List<Department> getAll();

    void save(Department department);

    PageInfo<Department> findByPage(Integer pageCode, Integer pageSize);
}
