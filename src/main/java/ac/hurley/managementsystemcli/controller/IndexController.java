package ac.hurley.managementsystemcli.controller;

import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/index")
@Controller
@Api(tags = "视图")
public class IndexController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/main")
    public String index() {
        return "main";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/build")
    public String build() {
        return "build";
    }

    @GetMapping("/login")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return "redirect:/index/home";
        }
        return "login";
    }

    @GetMapping("/users/pwd")
    public String updatePwd() {
        return "users/update_pwd";
    }

    @GetMapping("/users/info")
    public String userDetail(Model model) {
        model.addAttribute("flagType", "edit");
        return "users/user_edit";
    }

    @GetMapping("/users")
    public String userList() {
        return "users/user_list";
    }

    @GetMapping("/roles")
    public String roleList() {
        return "roles/role_list";
    }

    @GetMapping("/depts")
    public String deptList() {
        return "depts/dept_list";
    }

    @GetMapping("/logs")
    public String logList() {
        return "logs/log_list";
    }

    @GetMapping("/menus")
    public String menuList() {
        return "menu/menu_list";
    }

    @GetMapping("/sysContent")
    public String sysContent() {
        return "sysContent/list";
    }

    @GetMapping("/sysDict")
    public String sysDict() {
        return "sysDict/list";
    }

    @GetMapping("/sysJob")
    public String sysJob() {
        return "sysJob/list";
    }

    @GetMapping("/sysJobLog")
    public String sysJobLog() {
        return "sysJobLog/list";
    }

    @GetMapping("/sysFiles")
    public String sysFiles() {
        return "sysFiles/list";
    }

    @GetMapping("/sysGenerator")
    public String sysGenerator() {
        return "sysGenerator/list";
    }
}







