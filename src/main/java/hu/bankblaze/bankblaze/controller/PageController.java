package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.service.AdminService;

import hu.bankblaze.bankblaze.model.QueueNumber;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@AllArgsConstructor
public class PageController {

    @Autowired
    private AuthenticationManager authenticationManager;

    private AdminService adminService;
    private QueueNumberService queueNumberService;

    @GetMapping("/firstPage")
    public String startPage() {
        return "firstPage";
    }

    @GetMapping("/home")
    public String goHome (Model model) {
        model.addAttribute("newQueueNumber", new QueueNumber());
        return "home";
    }

    @PostMapping("/home")
    public String goHome(@ModelAttribute("newQueueNumber") QueueNumber queueNumber,
                         @RequestParam("whereTo") int id) {
        queueNumberService.addQueueNumber(queueNumber);
        switch (id) {
            case 1 -> {
                return "redirect:/retail";
            }
            case 2 -> {
                return "redirect:/corporate";
            }
            case 3 -> {
                return "redirect:/teller";
            }
            case 4 -> {
                queueNumberService.addQueueNumber(queueNumber);
                return "redirect:/premium";
            }
        }
        return "home";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public RedirectView checkLogin(HttpServletRequest request, @RequestParam("username") String userName,
                                   @RequestParam String password, RedirectAttributes redirectAttributes) {
        if (adminService.isAdmin(userName, password)) {
            List<GrantedAuthority> adminAuthorities = AuthorityUtils.createAuthorityList("ADMIN");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password,
                                                                                                adminAuthorities);

            if (SecurityContextHolder.getContext().getAuthentication() == null ||
                    SecurityContextHolder.getContext().getAuthentication()
                            .getClass().equals(AnonymousAuthenticationToken.class)) {
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            redirectAttributes.addFlashAttribute("message", "Admin Login Successful");
            return new RedirectView("redirect:/admin");

        } else if (adminService.isUser(userName, password)) {
            // Ügyintéző jogosultság hozzáadása
            List<GrantedAuthority> clerkAuthorities = AuthorityUtils.createAuthorityList("USER");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password,
                                                                                                clerkAuthorities);

            if (SecurityContextHolder.getContext().getAuthentication() == null ||
                    SecurityContextHolder.getContext().getAuthentication()
                            .getClass().equals(AnonymousAuthenticationToken.class)) {
                SecurityContextHolder.getContext().setAuthentication(token);
            }

            redirectAttributes.addFlashAttribute("message", "Employee Login Successful");
            return new RedirectView("redirect:/employee");
        } else {
            redirectAttributes.addFlashAttribute("message", "Invalid Username or Password");
            return new RedirectView("login");
        }
    }
    @GetMapping("/logout")
    public String showLogoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "firstPage";
    }

}
