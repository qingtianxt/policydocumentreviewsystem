package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.User;
import stdu.wzw.service.RoleService;
import stdu.wzw.service.UserService;
import stdu.wzw.utils.MD5Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 本模块实现用户功能。
 */
@RestController
@RequestMapping(value = {"/user"})
public class UserController {


    @Autowired
    private UserService userService;


    /**
     * 通过注册页面进入用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/register")
    public ModelAndView register(User user) {
        user.setCreateDate(new Date());
        user.setPassword(MD5Utils.md5(user.getPassword()));
        user.setRoleId(1);
        userService.save(user);
        return new ModelAndView("/page/login", "msg", "3");
    }

    @RequestMapping(value = "/login")
    public ModelAndView login(HttpServletRequest request, String username, String password) {
        User user = userService.getByUsername(username);

        if (null != user) {
            if (user.getPassword().equals(MD5Utils.md5(password))) {
                request.getSession().setAttribute("existUser", user);
                System.out.println(user);
                System.out.println("用户登录成功");
                return new ModelAndView("redirect:/page/index1");
            } else {
                //密码输入错误
                return new ModelAndView("redirect:/page/login", "msg", "2");
            }
        }
        //不存在该用户
        return new ModelAndView("redirect:/page/login", "msg", "3");
    }

    @RequestMapping(value = "/logout")
    public ModelAndView logout(HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("existUser");
        System.out.println(u);
        request.getSession().removeAttribute("existUser");
        request.getSession().invalidate();
        return new ModelAndView("redirect:/page/login", "msg", "4");
    }

    @RequestMapping(value = "/findByPage")
    public ModelAndView findByPage(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, String username, String msg) {
        PageInfo<User> list = userService.findByPage(pageCode, pageSize, username);
        ModelAndView mv = new ModelAndView("/user/clients", "page", list);
        if (null != username && !"".equals(username)) {
            mv.addObject("username", username);
        }
        if (null != msg && !"".equals(msg)) {
            mv.addObject("msg", msg);
        }
        return mv;
    }

    /**
     * ajax异步添加用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/add")
    public String add(User user) {
        user.setCreateDate(new Date());
        user.setPassword(MD5Utils.md5(user.getPassword()));
        userService.save(user);

        return "success";
    }

    @RequestMapping(value = "/delete")
    public ModelAndView delete(Integer userId) {
        userService.deleteById(userId);
        return new ModelAndView("redirect:/user/findByPage?msg=1");
    }
}
