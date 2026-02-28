package com.revpay.auth.controller;

import com.revpay.auth.service.ISecurityQuestionService;
import com.revpay.user.dto.CreateUserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthPageController {

    private final ISecurityQuestionService iSecurityQuestionService;

    @GetMapping("/personal/save")
    public String registerUser(Model model) {
        model.addAttribute("registerDto", new CreateUserRequestDto());
        model.addAttribute("questions", iSecurityQuestionService.getAllQuestions());
        return "auth/register-personal";
    }
}
