package stdu.wzw.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import stdu.wzw.model.SensitiveWord;
import stdu.wzw.service.SensitiveService;

@RestController
@RequestMapping(value = {"/sensitive"})
public class SensitiveController {
    @Autowired
    private SensitiveService sensitiveService;

    @RequestMapping("list")
    public ModelAndView list(@RequestParam(value = "pageCode", defaultValue = "1") Integer pageCode, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        PageInfo<SensitiveWord> list = sensitiveService.findByPage(pageCode, pageSize);
        return new ModelAndView("/sensitive/projects", "page", list);
    }

}
