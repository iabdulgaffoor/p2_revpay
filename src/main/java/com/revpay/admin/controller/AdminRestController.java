package com.revpay.admin.controller;

import com.revpay.auth.service.ISecurityQuestionService;
import com.revpay.domain.entity.auth.SecurityQuestion;
import com.revpay.domain.entity.user.Role;
import com.revpay.user.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminRestController {

    private final ISecurityQuestionService iSecurityQuestionService;
    private final IRoleService iRoleService;

    @PostMapping("/auth/add-security-question")
    public ResponseEntity<SecurityQuestion> addSecurityQuestion(
            @RequestBody SecurityQuestion securityQuestion
    ) {
        SecurityQuestion saved = iSecurityQuestionService.addQuestion(securityQuestion);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/role/add")
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        Role saveRole = iRoleService.saveRole(role);
        return new ResponseEntity<>(saveRole, HttpStatus.CREATED);
    }
}
