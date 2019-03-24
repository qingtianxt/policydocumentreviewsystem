package stdu.wzw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.Policy_review;
import stdu.wzw.service.Policy_reviewService;

/**
 * html页面转发
 */
@Controller
@RequestMapping(value = {"/page"})
public class PageController {
    @Autowired
    private Policy_reviewService policy_reviewService;

    /**
     * 首页加权限
     *
     * @return
     */
    @RequestMapping(value = {"index"})
    public String index() {
        return "index";
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(value = {"index1"})
    public String index1() {
        return "index1";
    }

    /**
     * 政策添加页面
     *
     * @return
     */
    @RequestMapping(value = {"form_file_upload"})
    public String form_file_upload() {
        return "policy/form_file_upload";
    }

    /**
     * 首页2
     *
     * @return
     */
    @RequestMapping(value = {"index_v2"})
    public String index_v2() {
        return "index_v2";
    }

    /**
     * 政策列表
     *
     * @return
     */
    @RequestMapping(value = {"table_basic"})
    public String table_basic() {
        return "policy/table_basic";
    }

    /**
     * 注册用户
     *
     * @return
     */
    @RequestMapping(value = {"register"})
    public String register() {
        return "register";
    }

    /**
     * 登录页面
     *
     * @return
     */
    @RequestMapping(value = {"login"})
    public String login() {
        return "login";
    }

    /**
     * 用户添加
     *
     * @return
     */
    @RequestMapping(value = {"user-add"})
    public String user_add() {
        return "user-add";
    }

    /**
     * 用户列表
     *
     * @return
     */
    @RequestMapping(value = {"clients"})
    public String clients() {
        return "user/clients";
    }

    @RequestMapping(value = {"article"})
    public String article() {
        return "article";
    }

    @RequestMapping(value = {"projects"})
    public String projects() {
        return "sensitive/projects";
    }

    @RequestMapping(value = {"compare"})
    public String compare() {
        return "compare";
    }

    @RequestMapping(value = {"compare2"})
    public String compare2() {
        return "compare2";
    }

    @RequestMapping(value = {"timeline_v2"})
    public String timeline_v2() {
        return "relationship/timeline_v2";
    }

    @RequestMapping(value = {"review_add"})
    public String review_add() {
        return "review/review_add";
    }

    @RequestMapping(value = {"department_list"})
    public String department_list() {
        return "department_list1";
    }

    @RequestMapping(value = {"graph_relative"})
    public ModelAndView graph_relative(@RequestParam(value = "reviewid") Integer reviewid) {
        Policy_review policy_review = policy_reviewService.findById(reviewid);
        ModelAndView mv = new ModelAndView("relationship/graph_relative");
        mv.addObject("policy_review", policy_review);
        return mv;
    }

    @RequestMapping(value = {"graph_echarts"})
    public String graph_echarts() {
        return "relationship/graph_echarts";
    }

    @RequestMapping(value = {"article_report1"})
    public String article_report1() {
        return "test/article-report1";
    }

    @RequestMapping(value = {"article_report2"})
    public String article_report2() {
        return "test/article-report2";
    }

    @RequestMapping(value = {"article_report3"})
    public String article_report3() {
        return "test/article-report3";
    }


    @RequestMapping(value = {"analysis_add"})
    public String analysis_add() {
        return "legitimacyAnalysis/analysis_add";
    }

    @RequestMapping(value = {"policy_info"})
    public String policy_info() {
        return "policy/policy_info";
    }

    @RequestMapping(value = {"search_results"})
    public String search_results() {
        return "relationship/search_results";
    }

    @RequestMapping(value = {"role_add"})
    public String role_add() {
        return "user/role_add";
    }
}
