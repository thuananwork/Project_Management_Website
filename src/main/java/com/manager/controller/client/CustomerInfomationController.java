package com.manager.controller.client;

import com.manager.DTO.GradeStudent;
import com.manager.DTO.ProjectGradeDTO;
import com.manager.DTO.ProjectGradeWeekDTO;
import com.manager.DTO.WeeklyProject;
import com.manager.FileService.StorageService;
import com.manager.model.*;
import com.manager.repository.*;
import com.manager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
public class CustomerInfomationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRegistrationRepository projectRegistrationRepository;
    @Autowired
    private WeeklyRequirementRepository weeklyRequirementRepository;
    @Autowired
    private WeeklyRequirementService weeklyRequirementService;

    @Autowired
    private StorageService storageService;
    @Autowired
    private StudentWeeklyReportsRepository studentWeeklyReportsRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private ProjectGradeService projectGradeService;
    @Autowired
    private ProjectGradeRepository projectGradeRepository;


    @GetMapping("/user/account/profile")
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
        return "pages/client/Profiles";
    }

    @PostMapping("/user/account/profile")
    public String updateProfile(@RequestParam(name = "id") Long id, @RequestParam(name = "lastname") String lastname, @RequestParam(name = "firstname") String firstname,
                                @RequestParam(name = "telephone") String phone, @RequestParam(name = "email") String email, @RequestParam(name = "gender-radio") String gender,
                                @RequestParam(name = "day") String day, @RequestParam(name = "month") String month, @RequestParam(name = "year") String year,
                                @RequestParam(name = "current_password") String oldPass, @RequestParam(name = "password") String newPass, @RequestParam(name = "confirmation") String confirm,
                                @RequestParam(name = "change_password") Optional<String> check, @RequestParam(name = "department") String department, @RequestParam(name = "code") String code,
                                @RequestParam(name = "className") String className
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
        user.setRole(roleRepository.findByRoleName("Student"));
        try {
            userService.addUser(user);
        } catch (Exception ex) {

        }
        user.setClassName(className);
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

    @GetMapping("/user/account/history")
    public String getHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        Project p = projectService.getAllProjectsByStatus(u.getId());
        Integer grade = null;
        if (p != null) {
            grade = projectGradeRepository.findStudentID(p.getProjectId());
        }
        String date = null;
        String status = null;
        ProjectRegistration pr = projectRegistrationRepository.findAllProjectByStatus(u.getId());
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
        if (pr != null) {
            date = formattedDate.format(pr.getRegistrationDate());
            status = String.valueOf(pr.getStatus());
        }
        model.addAttribute("date", date);
        model.addAttribute("grade", grade);
        model.addAttribute("status", status);
        model.addAttribute("projects", p);
        return "pages/client/ProjectHistory";
    }

    @GetMapping("/user/account/weekly")
    public String getWeekly(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        List<WeeklyProject> weeklyRequirement = weeklyRequirementService.getAllWeeklyProject(u.getId());
        Project p = projectService.getAllProjectsByStatus(u.getId());
        model.addAttribute("projects", p);
        model.addAttribute("weeklyRequirements", weeklyRequirement);
        return "pages/client/WeeklyRequirement";
    }

    @PostMapping("/user/account/submit_report")
    public String submitWeeklyReport(@RequestParam("requirementId") Long requirementId,
                                     @RequestParam("reportFile") MultipartFile reportFile,
                                     @RequestParam("comments") String comments,
                                     RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return "redirect:/login";
            }

            storageService.store(reportFile);
            // Tạo mới StudentWeeklyReport
            WeeklyRequirement requirement = weeklyRequirementRepository.findById(requirementId)
                    .orElseThrow(() -> new RuntimeException("Requirement not found"));

            StudentWeeklyReport report = new StudentWeeklyReport();
            report.setRequirement(requirement);
            report.setStudent(user);
            report.setSubmissionDate(new Date());
            report.setReportFilePath("/upload/" + reportFile.getOriginalFilename());
            report.setComments(comments);
            report.setSubmissionDate(new Date());
            studentWeeklyReportsRepository.save(report);

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setDescription("Bạn đã gửi thành công báo cáo ngày " + report.getSubmissionDate() + " !!!");
            notification.setTitle("Chúc mừng bạn");
            notification.setDate(new Date());
            notificationRepository.save(notification);

            redirectAttributes.addFlashAttribute("success", "Báo cáo đã được nộp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nộp báo cáo thất bại!");
            e.printStackTrace();
        }
        return "redirect:/user/account/weekly";
    }


    @GetMapping("/user/account/notify")
    public String getNotification(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        Set<Notification> list_notify = notificationRepository.findByUser_Id(u.getId());
        model.addAttribute("list_noti", list_notify);
        return "pages/client/nofi";
    }

    @GetMapping("/user/account/otp")
    public void sendOTP(HttpServletResponse response, @RequestParam(name = "email") String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (userRepository.findByEmail(email) != null) {
                response.getWriter().print("Email existed!");
                return;
            }
            Random rand = new Random();
            String otp = rand.nextInt(999999) + "";
            while (otp.length() < 6) {
                otp = "0" + otp;
            }
            String message = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                    "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                    "    <div style=\"border-bottom:1px solid #eee\">\n" +
                    "      <a href=\"\" style=\"font-size:1.4em;color: #C92127;text-decoration:none;font-weight:600\">Manager</a>\n" +
                    "    </div>\n" +
                    "    <p style=\"font-size:1.1em\">Hi,</p>\n" +
                    "    <p>Thank you for choosing Manager. Use the following OTP to confirm your Change EMail procedures. OTP is valid for 5 minutes</p>\n" +
                    "    <h2 style=\"background: #C92127;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" +
                    otp +
                    "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Regards,<br />Manager</p>\n" +
                    "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                    "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                    "      <p>Manager Inc</p>\n" +
                    "      <p>1600 Amphitheatre Parkway</p>\n";
            emailService.sendEmail(email, "Manager - Change OTP", message);
            response.getWriter().print(otp);
            User user = userRepository.findByEmail(auth.getName());
            user.setOtp(otp);
            user.setOtpRequestedTime(new Date());
            userService.addUser(user);
        } catch (Exception e) {

        }
    }

    @GetMapping("/user/account/grade_weekly")
    public String getGradeWeek(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        if (u == null) {
            return "redirect:/login";
        }
        List<ProjectGradeWeekDTO> weeklyRequirement = weeklyRequirementService.getAllWeeklyProjectGrade(u.getId());
        model.addAttribute("weeklyRequirements", weeklyRequirement);
        return "pages/client/GradeWeeklyRequirement";
    }
}
