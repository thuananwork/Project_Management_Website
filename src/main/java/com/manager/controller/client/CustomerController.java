package com.manager.controller.client;

import com.manager.model.*;
import com.manager.repository.*;
import com.manager.service.DefaultEmailService;
import com.manager.service.DefaultUserService;
import com.manager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Transactional
public class CustomerController {
    private final static String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DefaultEmailService emailService;

    @Autowired
    private DefaultUserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  NotificationRepository notificationRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectRegistrationRepository projectRegistrationRepository;

    @GetMapping("/signup")
    public String register(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("form", "signup");
        return "pages/client/login";
    }

    @PostMapping("/signup")
    public String register(@RequestParam("otp") String otp, Model model, HttpSession session) {
        User user = (User) session.getAttribute("register_user");
        if (otp.equals("")) {
            model.addAttribute("otp_message", "OTP is required.");
            model.addAttribute("form", "check");
            return "pages/client/login";
        }
        if (!otp.equals(user.getOtp())) {
            model.addAttribute("otp_message", "OTP is incorrect.");
            model.addAttribute("form", "check");
            return "pages/client/login";
        }
        if (new Date().getTime() - user.getOtpRequestedTime().getTime() > 300000) {
            model.addAttribute("otp_message", "OTP is expired.");
            model.addAttribute("form", "resent");
            return "pages/client/login";
        }
        try {
            session.removeAttribute("register_user");
            //set role for user
            user.setFirstName(user.getEmail().split("@")[0]);
            user.setLastName("");
            user.setActive(true);
            user.setRole(roleRepository.findByRoleName("Student"));
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setDescription("Hãy bắt đầu cuộc hành trình thú vị của bạn tại nơi đây");
            notification.setTitle("Chào mừng bạn");
            notification.setDate(new Date());

            userService.addUser(user);
            notificationRepository.save(notification);
        } catch (Exception e) {
        }
        return "redirect:/login";
    }

    @PostMapping("/signup/otp")
    public String sendOtp(@Valid User user, BindingResult result, Model model, @RequestParam("cf_password") String cfPassword, HttpSession session) {

        if (user.getPassword() != "" && !user.getPassword().matches(passwordRegex)) {
            model.addAttribute("form", "signup");
            result.rejectValue("password", "user.password", "Password must contain at least one digit, one lowercase, one uppercase, one special character and must be at least 8 characters long.");
        }

        if (result.hasFieldErrors("email") || result.hasFieldErrors("password")) {

            model.addAttribute("form", "signup");

            if (cfPassword.isEmpty())
                model.addAttribute("cf_password", "Confirm password is required");
            else if (!cfPassword.equals(user.getPassword()))
                model.addAttribute("cf_password", "Confirm password is not match");
            return "pages/client/login";
        }

        if (cfPassword.isEmpty()) {
            model.addAttribute("cf_password", "Confirm password is required");
            model.addAttribute("form", "signup");

            return "pages/client/login";
        } else if (!cfPassword.equals(user.getPassword())) {
            model.addAttribute("cf_password", "Confirm password is not match");
            model.addAttribute("form", "signup");

            return "pages/client/login";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            result.rejectValue("email", "user.email", "An account already exists for this email.");
            model.addAttribute("form", "signup");

            return "pages/client/login";
        }

        //send otp
        try {
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
                    "    <p>Thank you for choosing Manager. Use the following OTP to complete your Sign Up procedures. OTP is valid for 5 minutes</p>\n" +
                    "    <h2 style=\"background: #C92127;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" +
                    otp +
                    "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Regards,<br />Manager</p>\n" +
                    "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                    "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                    "      <p>Manager Inc</p>\n" +
                    "      <p>VietNam</p>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>";

            emailService.sendEmail(user.getEmail(), "Manager - Email Verification", message);
            user.setOtp(otp);
            user.setOtpRequestedTime(new Date());
            session.setAttribute("register_user", user);
            model.addAttribute("form", "check");
            return "pages/client/login";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("form", "login");
        return "pages/client/login";
    }

    @PostMapping("/login-failed")
    public String login(@Valid User user, BindingResult result, Model model) {
        if (result.hasFieldErrors("email") || result.hasFieldErrors("password")) {
            model.addAttribute("form", "login");
            return "pages/client/login";
        }
        User existUser = userRepository.findByEmail(user.getEmail());
        if (existUser == null || !BCrypt.checkpw(user.getPassword(), existUser.getPassword())) {
            model.addAttribute("form", "login");
            model.addAttribute("message", "Email hoặc Mật khẩu sai!");
            return "pages/client/login";
        }
        if (!existUser.isActive()) {
            model.addAttribute("form", "login");
            model.addAttribute("message", "Tài khoản chưa được kích hoạt!");
            return "pages/client/login";
        }
        //authentication
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("form", "forgot");
        return "pages/client/login";
    }

    @PostMapping("/forgot-password/otp")
    public String sendOtp(@Valid User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasFieldErrors("email")) {
            model.addAttribute("form", "forgot");
            return "pages/client/login";
        }
        User existUser = userRepository.findByEmail(user.getEmail());
        if (existUser == null) {
            model.addAttribute("form", "forgot");
            result.rejectValue("email", "user.email", "Email không tồn tại!");
            return "pages/client/login";
        }
        if(result.hasFieldErrors("password")) {
            model.addAttribute("user", new User());
        }
        //send otp
        try {
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
                    "    <p>Thank you for choosing Manager. Use the following OTP to confirm your Reset Password procedures. OTP is valid for 5 minutes</p>\n" +
                    "    <h2 style=\"background: #C92127;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" +
                    otp +
                    "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Regards,<br />Manager</p>\n" +
                    "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                    "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                    "      <p>Manager Inc</p>\n" +
                    "      <p>1600 Amphitheatre Parkway</p>\n";
            emailService.sendEmail(user.getEmail(), "Manager - Reset Password", message);
            user.setOtp(otp);
            user.setOtpRequestedTime(new Date());
            session.setAttribute("forgot_user", user);
            model.addAttribute("form", "forgot_check");
            return "pages/client/login";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/forgot-password")
    public  String forgetPassword(@RequestParam("otp") String otp, @Valid User user, BindingResult result, Model model, HttpSession session, @RequestParam("cf_password") String cfPassword) {
        boolean check = false;
        if (user.getPassword() == "" || !user.getPassword().matches(passwordRegex)) {
            check = true;
            if(user.getPassword() != "")
                result.rejectValue("password", "user.password", "Password must contain at least one digit, one lowercase, one uppercase, one special character and must be at least 8 characters long.");
        }
        if (cfPassword.isEmpty()) {
            model.addAttribute("cf_password", "Confirm password is required");
            check = true;
        } else if (!cfPassword.equals(user.getPassword())) {
            model.addAttribute("cf_password", "Confirm password is not match");
            check = true;
        }
        User forgotUser = (User) session.getAttribute("forgot_user");
        if (otp.equals("")) {
            model.addAttribute("otp_message", "OTP is required.");
            check = true;
        }
        else if (!otp.equals(forgotUser.getOtp())) {
            model.addAttribute("otp_message", "OTP is incorrect.");
            check = true;
        }
        if (new Date().getTime() - forgotUser.getOtpRequestedTime().getTime() > 300000) {
            model.addAttribute("otp_message", "OTP is expired.");
            check = true;
        }
        if(check) {
            model.addAttribute("form", "forgot_check");
            return "pages/client/login";
        }
        User existUser = userRepository.findByEmail(forgotUser.getEmail());
        userService.changePassword(existUser, user.getPassword());
        model.addAttribute("user", new User());
        model.addAttribute("form", "login");
        return "pages/client/login";
    }

    @PostMapping("/user/selectProject")
    public String selectProject(@RequestParam("projectId") Long projectId,
                                @RequestParam("studentId") Long studentId) {
        projectService.registerProject(projectId, studentId);
        return "redirect:pages/client/home";
    }

    @GetMapping("/project_registrations")
    public String projectRegistrations(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        List<Project> projects = projectService.getAllProjectsBy();
        model.addAttribute("projects", projects);
        return "pages/client/ProjectRegistration";
    }

    @GetMapping("/register")
    public String editProjectForm(@RequestParam(name = "id") Optional<Long> id, RedirectAttributes redirectAttributes) {
        Project p = null;
        if (id.isPresent()) {
            p = projectRepository.findById(id.get()).get();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        int alreadyRegistered = projectRegistrationRepository.existsByStudentID(user.getId());
        if (alreadyRegistered > 0) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đăng ký đồ án trước đó.");
            return "redirect:/project_registrations";
        }
        ProjectRegistration projectRegistration = new ProjectRegistration();
        projectRegistration.setProject(p);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email);
        projectRegistration.setStudent(u);
        projectRegistration.setStatus(ProjectRegistration.Status.ChoXacNhan);

        Date now = new Date();
        projectRegistration.setRegistrationDate(now);
        projectRegistrationRepository.save(projectRegistration);

        //lưu thông báo
        Notification notification = new Notification();
        notification.setUser(u);
        notification.setDescription("Bạn đăng ký đồ án " + p.getTitle() +  " thành công. Vui lòng chờ Giảng viên phê duyệt!");
        notification.setTitle("Chúc mừng bạn");
        notification.setDate(new Date());
        notificationRepository.save(notification);
        redirectAttributes.addFlashAttribute("success", "Đăng ký đồ án thành công!");
        return "redirect:/user/account/history";
    }
}