package com.manager.Filter;

import com.manager.model.Notification;
import com.manager.model.User;
import com.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class FilterUser implements Filter {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        Set<Notification> notificationSet = (Set<Notification>) session.getAttribute("noti");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)&& notificationSet == null) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email);
            notificationSet = user.getNotifications();
            req.setAttribute("authen_user", user);
        }
        else {
            notificationSet = new HashSet<>();
        }
        session.setAttribute("list_notification", notificationSet);

        //set referer except login
        String referer = req.getHeader("Referer");
        if(referer == null || referer.contains("signup")) {
            session.setAttribute("referer", "/");
        }
        else if (!referer.contains("login") && !referer.contains("css") && !referer.contains("/JS") && !referer.contains("/image")) {
            session.setAttribute("referer", referer);
        } else {
            session.setAttribute("referer", session.getAttribute("referer"));
        }


        chain.doFilter(request, response);
    }
}
