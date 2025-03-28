package com.manager.controller.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.manager.DTO.LecturerProjectCustom;
import com.manager.DTO.ProjectDTO;
import com.manager.FileService.CloudinaryService;
import com.manager.FileService.StorageService;
import com.manager.model.*;
import com.manager.repository.*;
import com.manager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@Transactional
public class AdminController {

    @Autowired
    private DefaultEmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DefaultUserService userServiceDefault;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LecturerProjectRepository projectServiceLecturerProjectRepository;

    @Autowired
    private LecturerProjectService lecturerProjectService;


    @Autowired
    private UserService userService;

    private final String UPLOAD_DIR = "src/main/resources/static/upload";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StorageService storageService;
    @Autowired
    private LecturerProjectRepository lecturerProjectRepository;
    @Autowired
    private WeeklyRequirementRepository weeklyRequirementRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ProjectLecturerRepository projectLecturerRepository;

    @GetMapping("/admin")
    public String index(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<User> admins = userRepository.findAllByRole(roleRepository.findByRoleName("Admin"));
        List<User> users = userRepository.findAll();
        users.removeAll(admins);
        List<Project> projects = projectRepository.findAll();
        Long totalUser = userRepository.countAllUsers();
        Long totalUserStudent = userRepository.countStudentUsers();
        Long totalUserLecturer = userRepository.countLecturerUsers();
        Long totalProject = projectRepository.countAllProjects();
        Long totalProjectIncomplete = projectRepository.countIncompleteProjects();
        Long totalProjectCompleted = projectRepository.countCompletedProjects();
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("totalUserStudent", totalUserStudent);
        model.addAttribute("totalUserLecturer", totalUserLecturer);
        model.addAttribute("totalProject", totalProject);
        model.addAttribute("totalProjectIncomplete", totalProjectIncomplete);
        model.addAttribute("totalProjectCompleted", totalProjectCompleted);
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("projects", projects);
        return "pages/admin/dashboard";
    }

    @GetMapping("/admin/projects")
    @ResponseBody
    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projectDTO = projectService.getAllProjectsConfirm();
        return projectDTO;
    }

    @GetMapping("/admin/projects/filter")
    @ResponseBody
    public List<ProjectDTO> filterProjects(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null || endDate == null) {
            // Nếu không có khoảng thời gian, trả về tất cả các dự án
            return projectService.getAllProjectsConfirm();
        }
        List<ProjectDTO> projects = projectService.getAllProjects(startDate, endDate);
        // Lọc dự án theo khoảng thời gian
        return new ArrayList<>(projects);
    }

    @GetMapping("/admin/create-account")
    public String show(Model model, @RequestParam(name = "success", defaultValue = "false") boolean success) {
        User user = new User();
        model.addAttribute("user", user);
        List<Role> roles = roleRepository.findAll();
        roles.remove(roleRepository.findByRoleName("Admin"));
//        roles.remove(roleRepository.findByRoleName("Student"));
        model.addAttribute("roles", roles);
        model.addAttribute("success", success);
        return "pages/admin/create_account";
    }

    @PostMapping("/admin/create-account")
    public String create(Model model, @Valid User user, BindingResult result, @RequestParam String role_id, @RequestParam(name = "date") String dob) throws ParseException {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String phone = user.getPhoneNumber();
        String address = user.getAddress();
        Date dateOfBirth = null;
        if (dob != null && !dob.isEmpty()) {
            dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
        }
        boolean active = true;
        boolean gender = user.isGender();
        Role role = roleRepository.findById(Long.parseLong(role_id));
        if(firstName==null || firstName.isEmpty()) {
            result.rejectValue("firstName", "firstName", "First Name is required.");
        }
        if(lastName==null || lastName.isEmpty()) {
            result.rejectValue("firstName", "firstName", "Last Name is required.");
        }
        if(email != null && !email.isEmpty() && userRepository.findByEmail(email)!=null) {
            result.rejectValue("email", "email", "Email already exists.");
        }
        if(phone == null || phone.isEmpty()) {
            result.rejectValue("phoneNumber", "error.user", "Phone Number is required.");
        }
        if(address == null || address.isEmpty()) {
            result.rejectValue("address", "error.user", "Address is required.");
        }
        if(result.hasFieldErrors("email") && result.hasFieldErrors("phoneNumber") && result.hasFieldErrors("address") && result.hasFieldErrors("firstName") && result.hasFieldErrors("lastName")) {
            List<Role> roles = roleRepository.findAll();
            roles.remove(roleRepository.findByRoleName("Admin"));
            roles.remove(roleRepository.findByRoleName("Student"));
            model.addAttribute("roles", roles);
            model.addAttribute("success", false);
            return "pages/admin/create_account";
        }
        user.setDob(dateOfBirth);
        user.setActive(active);
        user.setRole(role);
        String password = userService.generatePassword();
        user.setPassword(password);
        try{
            userService.addUser(user);
        } catch (Exception e) {
            List<Role> roles = roleRepository.findAll();
            roles.remove(roleRepository.findByRoleName("Admin"));
            roles.remove(roleRepository.findByRoleName("Student"));
            model.addAttribute("roles", roles);
            return "pages/admin/create_account";
        }
        //send password to mail
        try {
            String message = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                    "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                    "    <div style=\"border-bottom:1px solid #eee\">\n" +
                    "      <a href=\"\" style=\"font-size:1.4em;color: #C92127;text-decoration:none;font-weight:600\">Manager</a>\n" +
                    "    </div>\n" +
                    "    <p style=\"font-size:1.1em\">Hi,</p>\n" +
                    "    <p>Hi s. Use the following password to login into the system.</p>\n" +
                    "    <h2 style=\"background: #C92127;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" +
                    password +
                    "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Regards,<br />Manager</p>\n" +
                    "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                    "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                    "      <p>Manager Inc</p>\n" +
                    "      <p>1600 Amphitheatre Parkway</p>\n" +
                    "      <p>California</p>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>";

            emailService.sendEmail(user.getEmail(), "Manager - Password", message);
        } catch (Exception e) {

        }

        model.addAttribute("success", "Account created successfully.");
        return "redirect:/admin/create-account?success=true";
    }

    @GetMapping ("/admin/view-account")
    public String view(Model model, @RequestParam(name="email", defaultValue = "") String email, @RequestParam(name="role-id", defaultValue = "0") String id) {
        int roleId = Integer.parseInt(id);
        List<User> users;
        if(roleId==0) {
            users = userRepository.findAllByEmail(email);
            users.removeAll(userRepository.findAllByRole(roleRepository.findByRoleName("Admin")));
        } else {
            users = userRepository.findAllByEmailAndRoleId(email, roleId);
        }
        List<Role> roles = roleRepository.findAll();
        roles.remove(roleRepository.findByRoleName("Admin"));
        model.addAttribute("roles", roles);
        model.addAttribute("users", users);
        model.addAttribute("email", email);
        model.addAttribute("id", roleId);
        return "pages/admin/view_account";
    }
    @GetMapping ("/admin/Project/view-project")
    public String viewProject(Model model, @RequestParam(required = false) String status, @RequestParam(required = false) String keyword) {
        List<Project> projects;
        if (status == null || status.isEmpty() && (keyword == null || keyword.isEmpty())) {
            projects = projectRepository.findAll();
        } else if (status != null && !status.isEmpty()) {
            if (keyword != null && !keyword.isEmpty()) {
                projects = projectRepository.findAllBylikeStatuss(status, keyword); // Lọc theo trạng thái và từ khóa
            } else {
                projects = projectRepository.findAllByStatuss(status); // Lọc theo trạng thái
            }
        } else {
            projects = projectRepository.findAllBylikeKey(keyword); // Lọc theo từ khóa
        }
        for (Project project : projects) {
            if (project.getFile() != null) {
                String fileName = project.getFile().substring(project.getFile().lastIndexOf('/') + 1);
                project.setFile(fileName); // Đặt tên file lại trong property `file`
            }
        }
        model.addAttribute("status", status);
        model.addAttribute("projects", projects);
        return "pages/admin/Project/view_project";
    }

    @GetMapping ("/admin/Assignment/view-assignment")
    public String viewProjectAssignment(Model model, @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long lecturerId) {
        List<LecturerProjectCustom> projectCustoms = lecturerProjectService.getAllLecturerProject();
        model.addAttribute("projects", projectCustoms);
        return "pages/admin/Assignment/view";
    }

    @GetMapping ("/admin/Assignment/create-assignment")
    public String createProjectAssignment(Model model) {
        model.addAttribute("projects", lecturerProjectService.getAllProjects());
        model.addAttribute("lecturer", lecturerProjectService.getAllUser());
        model.addAttribute("assignment", new ProjectLecturer());
        return "pages/admin/Assignment/assignment";
    }

    @PostMapping("/admin/Assignment/create-assignment")
    public String saveProjectAssignment(@RequestParam(name = "id") Optional<Long> id,
                              @RequestParam(name = "lecturer",defaultValue = "0") Optional<Long> lecturer,
                              @RequestParam(name = "projects",defaultValue = "0") Optional<Long> project
    ) {
        ProjectLecturer p;
        if (!id.isPresent()) {
            p = new ProjectLecturer();
        } else {
            p = lecturerProjectRepository.findById(id.get()).get();
        }
        User user = userService.findUserById(lecturer.get());
        p.setLecturer(user);

        Project project_list = projectRepository.findProjectById(project.get());
        p.setProject(project_list);

        lecturerProjectRepository.save(p);
        return "redirect:/admin/Assignment/view-assignment";
    }

    @GetMapping("/admin/Assignment/edit")
    public String editProject(@RequestParam(name = "id") Optional<Long> id, Model model) {
        ProjectLecturer p = null;
        if (id.isPresent()) {
            p = lecturerProjectRepository.findById(id.get()).get();
        }
        model.addAttribute("projects", lecturerProjectService.getAllProjects());
        model.addAttribute("lecturer", lecturerProjectService.getAllUser());
        model.addAttribute("project", p);
        return "pages/admin/Assignment/edit";
    }

    @GetMapping("/admin/Assignment/delete")
    public String deleteAssignment(@RequestParam("id") Long id) {
        try {
            projectLecturerRepository.deleteById(id);

            return "redirect:/admin/Assignment/view-assignment";
        } catch (Exception e) {
            return "redirect:/admin/Assignment/view-assignment";
        }
    }

    @GetMapping("/admin/Project/create-project")
    public String addProjectForm(Model model,
                                 @RequestParam(name = "success", defaultValue = "false") boolean success) {
        List<User> list_user = userRepository.findAllUsersExcluding();
        model.addAttribute("activePage", "createProject");
        model.addAttribute("list_user", list_user);
        model.addAttribute("project", new Project());
        model.addAttribute("success", success);
        return "pages/admin/Project/create_project";
    }


    @PostMapping("/admin/Project/create-project")
    public String saveProject(@RequestParam(name = "projectId") Optional<Long> projectId,
                              @RequestParam(name = "title") String title,
                              @RequestParam(name = "description") String description,
                              @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              @RequestParam(name = "department") String department,
                              @RequestParam(name = "maxStudents") Integer maxStudents,
                              @RequestParam("files") MultipartFile files
                              ) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        Project p;
        if (!projectId.isPresent()) {
            p = new Project();
        } else {
            p = projectRepository.findById(projectId.get()).get();
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (totalDays < 7) {
            throw new IllegalArgumentException("Khoảng thời gian không đủ để tạo một tuần báo cáo!");
        }
        if(!projectId.isPresent()){
            String fileUrl = cloudinaryService.uploadFile(files);
//        storageService.store(files);
            p.setFile(fileUrl);
            p.setTitle(title);
            p.setCreatedBy(u);
            p.setDescription(description);
            p.setDepartment(department);
            p.setMaxStudents(maxStudents);
            p.setStatus(Project.Status.ChuaTienHanh);
            p.setStartDate(startDate);
            p.setEndDate(endDate);
            projectRepository.save(p);


        // Tính số tuần và chia ngày theo tuần
        int weekCount = (int) Math.ceil((double) totalDays / 7);
        int daysPerWeek = 7;
        int remainingDays = (int) (totalDays % 7);

        LocalDate currentStartDate = startDate;

        for (int weekNumber = 1; weekNumber <= weekCount; weekNumber++) {
            LocalDate currentEndDate = currentStartDate.plusDays(daysPerWeek - 1);

            if (weekNumber == weekCount && remainingDays > 0) {
                currentEndDate = currentEndDate.plusDays(remainingDays);
            }

            WeeklyRequirement weeklyRequirement = new WeeklyRequirement();
            weeklyRequirement.setProject(p);
            weeklyRequirement.setWeekNumber(weekNumber);
            weeklyRequirement.setStartDate(currentStartDate);
            weeklyRequirement.setEndDate(currentEndDate);
            weeklyRequirement.setDescription("Báo cáo tuần " + weekNumber);
            weeklyRequirementRepository.save(weeklyRequirement);
            currentStartDate = currentEndDate.plusDays(1);
        }
        }else {
            String fileUrl = cloudinaryService.uploadFile(files);
//        storageService.store(files);
            p.setFile(fileUrl);
            p.setTitle(title);
            p.setCreatedBy(u);
            p.setDescription(description);
            p.setDepartment(department);
            p.setMaxStudents(maxStudents);
            p.setStartDate(startDate);
            p.setEndDate(endDate);
            projectRepository.save(p);
        }
        return "redirect:/admin/Project/view-project";
    }

    @GetMapping("/admin/Project/delete")
    public String deleteProject(@RequestParam("id") Long id) {
        try {

            projectRepository.deleteWeeklyRequirementsByProjectId(id);
            projectRepository.deleteProjectById(id);

            return "redirect:/admin/Project/view-project";
        } catch (Exception e) {
            return "redirect:/admin/Project/view-project";
        }
    }

    @GetMapping("/admin/Project/edit")
    public String editProjectForm(@RequestParam(name = "id") Optional<Long> id, Model model) {
        Project p = null;
        if (id.isPresent()) {
            p = projectRepository.findById(id.get()).get();
        }
        List<User> list_user = userRepository.findAll();
        model.addAttribute("list_user", list_user);
        model.addAttribute("project", p);
        return "pages/admin/Project/edit_project";
    }

    @GetMapping("/admin/view-detail")
    public String viewDetail(Model model, @RequestParam(name="id") long id ) {
        User user = userRepository.findById(id);
        model.addAttribute("user", user);
        return "pages/admin/view_detail";
    }

    @GetMapping("/admin/activate")
    public String activate(Model model, @RequestParam(name="id") long id ) {
        User user = userRepository.findById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return "redirect:/admin/view-account";
    }

    @GetMapping("/admin/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("fileName") String fileName) throws MalformedURLException {
        // Tải file từ đường dẫn
        try {
            // Lấy file từ URL trên Cloudinary
            URL url = new URL(fileName);
            InputStream inputStream = url.openStream();

            // Lấy tên file từ URL
            String fileUrl = fileName.substring(fileName.lastIndexOf("/") + 1);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileUrl + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            throw new RuntimeException("Không thể tải file từ Cloudinary: " + e.getMessage());
        }
    }
}
