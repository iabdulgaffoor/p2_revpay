package com.revpay.auth.controller;

import com.revpay.auth.service.ISecurityQuestionService;
import com.revpay.domain.entity.auth.SecurityQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final ISecurityQuestionService iSecurityQuestionService;
}
