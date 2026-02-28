package com.revpay.admin.controller;

import com.revpay.domain.entity.auth.SecurityQuestion;
import com.revpay.domain.entity.user.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping("/auth/add-security-question")
    public String addSecurityQuestion(Model model) {
        model.addAttribute("securityQuestion", new SecurityQuestion());
        return "admin/add-security-question";
    }

    @GetMapping("/role/add")
    public String addRole(Model model) {
        model.addAttribute("role", new Role());
        return "admin/add-role";
    }
}
