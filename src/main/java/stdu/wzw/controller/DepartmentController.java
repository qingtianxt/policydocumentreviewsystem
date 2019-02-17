package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.Department;
/*import stdu.wzw.modeljpa.DepartmentJpa;
import stdu.wzw.repository.DepartmentRepository;
import stdu.wzw.repository.PlaceRepository;*/
import stdu.wzw.service.DepartmentService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = {"/department"})
public class DepartmentController {


    @Autowired
    private DepartmentService departmentService;

   /* @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @RequestMapping(value = "/test")
    public Page<DepartmentJpa> test() {
        Integer pageCode = 1;
        Integer pageSize = 10;
        return departmentRepository.findAll(new PageRequest(pageCode, pageSize, new Sort(Sort.Direction.DESC, "departmentId")));
    }*/

    @RequestMapping(value = "/list")
    public ModelAndView list(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, String msg) {
        PageInfo<Department> list = departmentService.findByPage(pageCode, pageSize);
        System.out.println(list);
        ModelAndView mv = new ModelAndView("/department/department_list", "page", list);
        mv.addObject("msg", msg);
        return mv;
    }


    @RequestMapping(value = "/getByParentId")
    public List<Department> getByParentId(Integer parentId) {
        List<Department> list = departmentService.getByParentId(parentId);
        return list;
    }

    @RequestMapping(value = "/getById")
    public Department getById(Integer departmentId) {
        Department department = departmentService.getByDepartmentId(departmentId);
        return department;
    }

    @RequestMapping(value = "/getAll")
    public List<Department> getAll() {
        List<Department> list = departmentService.getAll();
        return list;
    }

    @RequestMapping(value = "/save")
    public ModelAndView save(Department department) {
        department.setCreateDate(new Date());
        if (department.getParentId() != null) {
            Department d = departmentService.getByDepartmentId(department.getParentId());
            department.setGrade(d.getGrade() + 1);
        } else {
            department.setGrade(1);
        }
        departmentService.save(department);
        ModelAndView modelAndView = new ModelAndView("redirect:/department/list?msg=1");
        return modelAndView;
    }
}
