package com.revpay.auth.service;

import com.revpay.domain.entity.auth.SecurityQuestion;

public interface ISecurityQuestionService {
    SecurityQuestion addQuestion(SecurityQuestion securityQuestion);
}
