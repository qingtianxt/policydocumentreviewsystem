package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.mapper.PowerMapper;
import stdu.wzw.model.Power;
import stdu.wzw.model.Role;
import stdu.wzw.service.RoleService;
import stdu.wzw.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = {"/role"})
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PowerMapper powerMapper;


    @RequestMapping(value = "/getAllRole")
    public List<Role> getAllRole() {
        return roleService.findAll();
    }

    @RequestMapping(value = "/getPower")
    public ModelAndView getPower(Integer roleId) {
        ModelAndView mv = new ModelAndView("/user/role_info");
        Role role = roleService.findByRoleId(roleId);
        mv.addObject("role", role);
        String power = role.getPower();
        String[] split = power.split(",");

        List<Power> powerList = new ArrayList<>();
        for (String s : split
                ) {
            int powerId = StringUtil.StringToInt(s);
            powerList.add(powerMapper.selectByPrimaryKey(powerId));
        }
        List<Power> powerAll = powerMapper.findAll();
        mv.addObject("powerAll", powerAll);
        mv.addObject("powerList", powerList);
        return mv;
    }

    /**
     * 删除一个角色
     *
     * @return
     */
    @RequestMapping(value = "/delete")
    public ModelAndView delete(Integer roleId) {
        ModelAndView mv = new ModelAndView("redirect:/role/findByPage");
        roleService.delete(roleId);
        mv.addObject("msg", 2);
        return mv;
    }

    /**
     * 添加一个角色
     *
     * @return
     */
    @RequestMapping(value = "/save")
    public ModelAndView save(String roleName, String power) {
        ModelAndView mv = new ModelAndView("redirect:/role/findByPage");
        Role role = new Role();
        role.setCreateDate(new Date());
        role.setRoleName(roleName);
        role.setPower(power);
        roleService.add(role);
        mv.addObject("msg", 1);
        return mv;
    }

    /**
     * 查询所有权限返回
     *
     * @return
     */
    @RequestMapping(value = "/addRole")
    public ModelAndView addRole() {
        ModelAndView mv = new ModelAndView("/user/role_add");
        List<Power> powerList = powerMapper.findAll();
        mv.addObject("powerList", powerList);
        System.out.println(powerList.size());
        return mv;
    }


    @RequestMapping(value = "/findByPage")
    public ModelAndView findByPage(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, String msg) {
        PageInfo<Role> list = roleService.findByPage(pageCode, pageSize);
        ModelAndView mv = new ModelAndView("/user/role_list", "page", list);
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }

}
