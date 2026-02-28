package com.revpay.auth.service.impl;

import com.revpay.auth.repository.ISecurityQuestionRepo;
import com.revpay.auth.service.ISecurityQuestionService;
import com.revpay.domain.entity.auth.SecurityQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityQuestionServiceImpl implements ISecurityQuestionService {

    private final ISecurityQuestionRepo iSecurityQuestionRepo;

    @Override
    public SecurityQuestion addQuestion(SecurityQuestion securityQuestion) {
        return iSecurityQuestionRepo.save(securityQuestion);
    }
}
