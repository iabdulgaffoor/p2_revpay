package com.revpay.auth.service.impl;

import com.revpay.auth.repository.ISecurityQuestionRepo;
import com.revpay.auth.repository.IUserSecurityAnswerRepo;
import com.revpay.auth.service.ISecurityAnswerService;
import com.revpay.common.exception.auth.SecurityQuestionNotFoundException;
import com.revpay.domain.entity.auth.SecurityQuestion;
import com.revpay.domain.entity.auth.UserSecurityAnswer;
import com.revpay.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityAnswerServiceImpl implements ISecurityAnswerService {

    private final IUserSecurityAnswerRepo iUserSecurityAnswerRepo;
    private final ISecurityQuestionRepo iSecurityQuestionRepo;

    @Override
    @Transactional
    public void addUserSecurityAnswer(String securityAnswer, User user, Long securityQuestionId) {
        Optional<SecurityQuestion> question = iSecurityQuestionRepo.findById(securityQuestionId);
        if (question.isEmpty()) {
            throw new SecurityQuestionNotFoundException(
                    "the registered id does not have any question consult Admin for more info"
            );
        }
        iUserSecurityAnswerRepo.save(
                new UserSecurityAnswer(
                        securityAnswer,
                        user,
                        question.get()
                )
        );
    }
}
