package com.revpay.auth.service;

import com.revpay.domain.entity.auth.SecurityQuestion;

import java.util.List;

public interface ISecurityQuestionService {
    SecurityQuestion addQuestion(SecurityQuestion securityQuestion);
    List<SecurityQuestion> getAllQuestions();
}
