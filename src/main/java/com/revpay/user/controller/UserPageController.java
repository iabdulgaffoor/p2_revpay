package com.revpay.user.controller;

import com.revpay.user.dto.CreateUserRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/save")
    public String registerUser(Model model) {
        model.addAttribute("registerDto", new CreateUserRequestDto());
        return "auth/register-personal";
    }


}
