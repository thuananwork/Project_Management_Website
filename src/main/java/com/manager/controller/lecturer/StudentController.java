package com.manager.controller.lecturer;

import com.manager.DTO.*;
import com.manager.model.*;
import com.manager.repository.*;
import com.manager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class StudentController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WeeklyRequirementService weeklyRequirementService;
    @Autowired
    private WeeklyRequirementRepository weeklyRequirementRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectRegistrationRepository projectRegistrationRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private StudentWeeklyReportsRepository studentWeeklyReportsRepository;
    @Autowired
    private ProjectGradeRepository projectGradeRepository;
    @Autowired
    private ProjectGradeService projectGradeService;
    @Autowired
    private LecturerProjectRepository lecturerProjectRepository;
    @Autowired
    private LecturerProjectService lecturerProjectService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DefaultUserService userService;

    @GetMapping("/lecturer")
    public String getDashBoard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        List<ProjectConfirm> project = projectService.getAllProjectsConfirm(u.getId());
        Integer p = projectRepository.findAllProjectConfirms(u.getId());
        Integer pj = projectRepository.findAllProjectConfirmss(u.getId());
        model.addAttribute("weeklyRequirement", new WeeklyRequirement());
        model.addAttribute("count", p);
        model.addAttribute("counts", pj);
        model.addAttribute("projects", project);
        return "pages/lecturer/dashboard";
    }

    @GetMapping("/lecturer/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        if (user != null) {
            Calendar cal = Calendar.getInstance();
            if (user.getDob() != null) {
                cal.setTime(user.getDob());
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                model.addAttribute("day", day);
                model.addAttribute("month", month + 1);
                model.addAttribute("year", year);
            }
        }
        model.addAttribute("user", user);
        return "pages/lecturer/Profiles";
    }

    @PostMapping("/lecturer/profile")
    public String updateProfile(@RequestParam(name = "id") Long id,
                                @RequestParam(name = "lastname") String lastname,
                                @RequestParam(name = "firstname") String firstname,
                                @RequestParam(name = "telephone") String phone,
                                @RequestParam(name = "email") String email,
                                @RequestParam(name = "gender-radio") String gender,
                                @RequestParam(name = "day") String day,
                                @RequestParam(name = "month") String month,
                                @RequestParam(name = "year") String year,
                                @RequestParam(name = "current_password") String oldPass,
                                @RequestParam(name = "password") String newPass,
                                @RequestParam(name = "confirmation") String confirm,
                                @RequestParam(name = "change_password") Optional<String> check,
                                @RequestParam(name = "department") String department,
                                @RequestParam(name = "code") String code
            , HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = userRepository.findById(id).get();
        String date_String = year + "-" + day + "-" + month;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
        try {
            formatter.setLenient(false);
            Date date = formatter.parse(date_String);
            user.setDob(date);
        } catch (ParseException e) {
            model.addAttribute("error", "date wrong!");
            return showProfile(model);
        }
        if (check.isPresent()) {
            if (oldPass != null && BCrypt.checkpw(oldPass, user.getPassword())) {
                if (newPass.equals(confirm)) {
                    user.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt()));
                } else {
                    model.addAttribute("error", "Password and Re-Password not match");
                    return showProfile(model);
                }
            } else {
                model.addAttribute("error", "Old Password is wrong!");
                return showProfile(model);
            }
        }
        user.setRole(roleRepository.findByRoleName("Lecturer"));
        try {
            userService.addUser(user);
        } catch (Exception ex) {

        }
        user.setCode(code);
        user.setDepartment(department);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPhoneNumber(phone);
        user.setEmail(email);
        user.setGender(gender.equalsIgnoreCase("male"));
        model.addAttribute("report", "Save successful!!");
        return showProfile(model);
    }

    @GetMapping("/lecturer/list_StudentWeeklyReport")
    public String listWeeklyRequirements(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        List<Integer> weekNumbers = weeklyRequirementService.getAvailableWeekNumbers();
        List<WeeklyRequirement> requirements = weeklyRequirementService.getAllWeeklyRequirements(u.getId());
        List<Double> grade = weeklyRequirementRepository.findAllProjectByGrade(u.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        requirements.forEach(req -> {
            req.setStartDate(LocalDate.parse(req.getStartDate().format(formatter)));
            req.setEndDate(LocalDate.parse(req.getEndDate().format(formatter)));
        });
        model.addAttribute("weeklyRequirement", requirements);
        model.addAttribute("grade", grade);
        model.addAttribute("weekNumbers", weekNumbers);
        return "pages/lecturer/StudentWeeklyReport/StudentWeeklyReport";
    }

    @GetMapping("/lecturer/create_StudentWeeklyReport")
    public String getHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        List<Project> project = projectRepository.findAllProject(u.getId());
        model.addAttribute("weeklyRequirement", new WeeklyRequirement());
        model.addAttribute("projects", project);
        return "pages/lecturer/StudentWeeklyReport/AddStudentWeeklyReport";
    }

    @GetMapping("/lecturer/get-weeks")
    @ResponseBody
    public List<Map<String, String>> getWeeks(@RequestParam Long projectId) {
        return weeklyRequirementService.calculateWeeks(projectId);
    }

    @GetMapping("/lecturer/edit_StudentWeeklyReport")
    public String editWeeklyReport(@RequestParam(name = "id") Optional<Long> id, Model model) {
        WeeklyRequirement p = null;
        if (id.isPresent()) {
            p = weeklyRequirementRepository.findById(id.get()).get();
        }
        model.addAttribute("weeklyRequirement", p);
        model.addAttribute("projects", projectService.getAllProjects());
        return "pages/lecturer/StudentWeeklyReport/EditStudentWeeklyReport";
    }

    @PostMapping("/lecturer/delete")
    public String deleteWeeklyRequirement(@RequestParam(name = "id") Long id, Model model) {
        weeklyRequirementService.deleteWeeklyRequirement(id);
        return "redirect:/lecturer/list_StudentWeeklyReport";
    }

    @PostMapping("/lecturer/create-weekly-reports")
    public String createWeeklyReports(@RequestParam Long projectId,
                                      @RequestParam Map<String, String> weeklyRequirements,
                                      RedirectAttributes redirectAttributes) {
        try {

            List<WeeklyRequirementDTO> dtoList = new ArrayList<>();
            int index = 0;

            // Gom nhóm dữ liệu từ Map vào danh sách DTO
            while (weeklyRequirements.containsKey("weeklyRequirements[" + index + "].description")) {
                WeeklyRequirementDTO dto = new WeeklyRequirementDTO();
                dto.setDescription(weeklyRequirements.get("weeklyRequirements[" + index + "].description"));
                dto.setStartDate(LocalDate.parse(weeklyRequirements.get("weeklyRequirements[" + index + "].startDate")));
                dto.setEndDate(LocalDate.parse(weeklyRequirements.get("weeklyRequirements[" + index + "].endDate")));
                dto.setWeekNumber(weeklyRequirements.get("weeklyRequirements[" + index + "].weekNumber"));

                dtoList.add(dto);
                index++;
            }
            weeklyRequirementService.saveWeeklyReports(projectId, dtoList);

            redirectAttributes.addFlashAttribute("success", "Tạo yêu cầu báo cáo tuần thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        return "redirect:/lecturer/list_StudentWeeklyReport";
    }

//    @PostMapping("/lecturer/create_StudentWeeklyReport")
//    public String saveProject(@RequestParam Long projectId,
//                              @RequestParam Map<String, String>[] weeklyRequirements,
//                              RedirectAttributes redirectAttributes
//    ) {
//
//        List<Project> projectList = projectRepository.findById(projectId);
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đồ án"));
//
//        for (Map<String, String> requirement : weeklyRequirements) {
//            WeeklyRequirement weeklyRequirement = new WeeklyRequirement();
//            weeklyRequirement.setProject(project);
//            weeklyRequirement.setWeekNumber(Integer.parseInt(requirement.get("weekNumber")));
//            weeklyRequirement.setStartDate(LocalDate.parse(requirement.get("startDate")));
//            weeklyRequirement.setEndDate(LocalDate.parse(requirement.get("endDate")));
//            weeklyRequirement.setDescription(requirement.get("description"));
//
//            weeklyRequirementRepository.save(weeklyRequirement);
//        }
//        WeeklyRequirement w;
//        if (!requirementId.isPresent()) {
//            w = new WeeklyRequirement();
//        } else {
//            w = weeklyRequirementRepository.findById(requirementId.get()).get();
//        }
//        Project p = projectService.findProjectById(project.get());
//        ProjectRegistration pr = projectRegistrationRepository.findAllProjectByUserID(p.getProjectId());
//        w.setProject(p);
//        w.setDescription(description.get());
//        w.setStartDate(startDate.get());
//        w.setEndDate(endDate.get());
//        weeklyRequirementRepository.save(w);

//        //add notification
//        Notification notification = new Notification();
//        notification.setUser(pr.getStudent());
//        notification.setTitle("Đồ án " + p.getTitle() + " của bạn có lịch cho quá trình làm đồ án!!!");
//        notification.setDescription("Đồ án " + p.getTitle() + " của bạn có lịch cho quá trình làm đồ án!!!");
//        notificationRepository.save(notification);
//        return "redirect:/lecturer/list_StudentWeeklyReport";
//    }

    @GetMapping("/lecturer/evaluation")
    public String getEvaluation(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        List<Integer> weekNumber = evaluationRepository.findAllProjectsWeek(u.getId());
        List<EvaluationDTO> evaluations = evaluationService.getAllWeeklyProjects(u.getId());

        model.addAttribute("weekNumber", weekNumber);
        model.addAttribute("evaluations", evaluations);
        return "pages/lecturer/Evaluations";
    }

//    @GetMapping("/download/{reportId}")
//    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
//        try {
//            // Lấy thông tin file từ database
//            StudentWeeklyReport report = studentWeeklyReportsRepository.findById(reportId)
//                    .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
//
//            String filePath = report.getReportFilePath(); // Đường dẫn tới file trong database
//
//            // Kết hợp đường dẫn và lấy file
//            Path file = fileStorageLocation.resolve(filePath).normalize();
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                throw new RuntimeException("File not found or not readable: " + filePath);
//            }
//        } catch (MalformedURLException ex) {
//            throw new RuntimeException("Error in file path", ex);
//        }
//    }

    @PostMapping("/lecturer/grade")
    public ResponseEntity<String> submitGrade(@RequestParam Long reportId,
                                              @RequestParam BigDecimal grade,
                                              @RequestParam String feedback,
                                              Authentication authentication) {
        String email = authentication.getName();
        User lecturer = userRepository.findByEmail(email);

        if (lecturer == null) {
            return ResponseEntity.badRequest().body("Giảng viên không tồn tại.");
        }

        StudentWeeklyReport report = studentWeeklyReportsRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Báo cáo không tồn tại"));
        Evaluation evaluation = new Evaluation();
        evaluation.setStudentReport(report);
        evaluation.setLecturer(lecturer);
        evaluation.setGrade(grade);
        evaluation.setFeedback(feedback);
        evaluation.setEvaluationDate(new Date());
        evaluationRepository.save(evaluation);

        report.setWeeklyReportGrade(grade.doubleValue());
        studentWeeklyReportsRepository.save(report);

        Notification notification = new Notification();
        notification.setUser(report.getStudent());
        notification.setDescription("Bạn đã được Giảng viên " + lecturer.getFirstName() + lecturer.getLastName() + " chấm điểm cho báo cáo!!!");
        notification.setTitle("Chúc mừng bạn");
        notification.setDate(new Date());
        notificationRepository.save(notification);
        return ResponseEntity.ok("Chấm điểm thành công");
    }

    @GetMapping("/lecturer/projectGrade")
    public String getProjectGrade(@RequestParam(name = "id") Optional<Long> id, Model model, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        List<ProjectGradeDTO> projectGrade = projectGradeService.getAllProjectGrade();
        model.addAttribute("projectGrades", projectGrade);
        return "pages/lecturer/ProjectGrade";
    }

    @PostMapping("/lecturer/projectGrades")
    @ResponseBody
    public String saveProjectGrade(@RequestParam Long projectId,
                                   @RequestParam BigDecimal finalGrade,
                                   @RequestParam String comments,
                                   Principal principal) {
        User lecturer = userRepository.findByEmail(principal.getName());
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User student = projectGradeService.getAllProjectGradeStudent(project.getProjectId());

        ProjectGrade projectGrade = new ProjectGrade();
        projectGrade.setProject(project);
        projectGrade.setLecturer(lecturer);
        projectGrade.setStudent(student);
        projectGrade.setFinalGrade(finalGrade);
        projectGrade.setComments(comments);
        projectGrade.setGradeDate(new Date());
        projectGradeRepository.save(projectGrade);

        project.setStatus(Project.Status.HoanThanh);
        projectRepository.save(project);

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setDescription("Bạn đã được Giảng viên " + lecturer.getFirstName() + lecturer.getLastName() + " chấm điểm cho đồ án của bạn!!!");
        notification.setTitle("Chúc mừng bạn đã hoàn thành đồ án");
        notification.setDate(new Date());
        notificationRepository.save(notification);

        return "Grade saved successfully!";
    }

    @GetMapping("/lecturer/confirm")
    public String getConfirm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        List<ProjectConfirm> project = projectService.getAllProjectsConfirm(u.getId());
        model.addAttribute("weeklyRequirement", new WeeklyRequirement());
        model.addAttribute("projects", project);
        return "pages/lecturer/Confirm";
    }


    @PostMapping("/lecturer/regisConfirm")
    @ResponseBody
    public ResponseEntity<String> saveProjectConfirm(@RequestParam Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Kiểm tra trạng thái hiện tại trước khi cập nhật
            if (project.getStatus() == Project.Status.ChuaTienHanh) {
                project.setStatus(Project.Status.DangTienHanh);
                projectRepository.save(project);

                // Cập nhật trạng thái trong ProjectRegistration (nếu có)
                ProjectRegistration registration = projectRegistrationRepository.findAllProjectByUserID(project.getProjectId());

                registration.setStatus(ProjectRegistration.Status.XacNhan);
                projectRegistrationRepository.save(registration);
                Notification notification = new Notification();
                notification.setUser(registration.getStudent());
                notification.setDescription("Đồ án của bạn đã được xác nhận thành công!!!");
                notification.setTitle("Chúc mừng bạn đã đăng ký đồ án thành công");
                notification.setDate(new Date());
                notificationRepository.save(notification);

                return ResponseEntity.ok("Đồ án đã được xác nhận thành công!");
            } else {
                return ResponseEntity.badRequest().body("Trạng thái đồ án không phù hợp để xác nhận!");
            }
        }

        return ResponseEntity.badRequest().body("Không tìm thấy đồ án!");
    }
}
