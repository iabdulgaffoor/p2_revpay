package com.revpay.auth.service;

import com.revpay.domain.entity.user.User;

public interface ISecurityAnswerService {
    void addUserSecurityAnswer(String securityAnswer, User user, Long securityQuestionId);
}
