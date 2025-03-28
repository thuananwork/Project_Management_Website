package com.manager.controller.client;

import com.manager.model.Project;
import com.manager.repository.*;
import com.manager.service.ProjectService;
import com.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "pages/client/home_main";
    }

    @GetMapping("/home")
    public String getAvailableProjects(Model model) {
        // Fetch all projects
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "pages/client/home";
    }
    @GetMapping("/loadMenu")
    public void LoadMenu(@RequestParam(name = "id", defaultValue = "1") Long id, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset = UTF-8");
        Project Parent_cate = projectService.findProjectById(id);
//        Set<Project> list_subCategory = projectRepository.findByParentProject_Id(id);
        response.getWriter().print("<div class=\"fhs_column_stretch\" style=\"padding-right: 12px;\">\n" +
                "                                <div class=\"fhs_menu_title fhs_center_left\" style=\"padding-left: 24px;\"><i\n" +
                "                                        class=\"ico_sachtrongnuoc\"></i><span class=\"menu-title\">" + Parent_cate.getTitle() + "</span><b\n" +
                "                                        class=\"caret\"></b></div>\n" +
                "                                <div class=\"fhs_menu_content fhs_column_left\">\n" +
                "                                    <div class=\"row\">");
        int sum = 0;
//        for (Project i : list_subCategory) {
//            sum++;
//            if (sum > 8) {
//                break;
//            }
//            response.getWriter().print("<div class=\"mega-col col-sm-3 \" data-widgets=\"wid-98\" data-colwidth=\"3\">\n" +
//                    "                                            <div class=\"mega-col-inner\">\n" +
//                    "                                                <div class=\"ves-widget\" data-id=\"wid-98\">\n" +
//                    "                                                    <div class=\"widget-html\">\n" +
//                    "                                                        <div class=\"widget-inner\">\n" +
//                    "                                                            <h3><span style=\"color: #333333;\"><strong><a\n" +
//                    "                                                                    style=\"font-weight: bold; font-size: 13px;\"\n" +
//                    "                                                                    href=\"category?id="+i.getProjectId() + "\"><span\n" +
//                    "                                                                    style=\"color: #333333;\">" + i.getTitle() +
//                    "                                                                                </span></a></strong></span></h3>\n" +
//                    "                                                            <ul class=\"nav-links\">");
//            response.getWriter().print("                                                       </ul>\n" +
//                    "                                                        </div>\n" +
//                    "                                                    </div>\n" +
//                    "                                                </div>\n" +
//                    "                                            </div>\n" +
//                    "                                        </div>");
//        }
    }

    @GetMapping("/loadProduct")
    public void LoadProduct(@RequestParam(name = "id") String id, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset = UTF-8");
        response.getWriter().print("<ul class=\"bxslider\">\n" +
                "<li class=\"item items-sl-width\">");
        response.getWriter().print("                                        </li>\n" +
                "                                    </ul>\n" +
                "                                        <a style=\"    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: center;\n" +
                "    max-width: 100%;\n" +
                "    max-height: 100%;\n" +
                "    border-radius: 8px;\n" +
                "    cursor: pointer;\n" +
                "    user-select: none;\n" +
                "    transition: all 0.5s;\n" +
                "    color: #C92127;\n" +
                "    background-color: #fff;\n" +
                "    border: 2px solid #C92127;\n" +
                "    font-size: 14px;\n" +
                "    font-weight: 700;\n" +
                "    width: 210px;\n" +
                "    height: 40px;    margin: 0 auto;\"   href=\"search?name=\">Xem Thêm</a>\n");
    }

    @GetMapping("/product")
    public String product(Model model, @RequestParam(name = "id") long id, Principal principal) {
        return "pages/client/Project";
    }


}
