package com.revpay.auth.service.impl;

import com.revpay.auth.repository.IPasswordRepo;
import com.revpay.auth.service.IPasswordService;
import com.revpay.domain.entity.auth.Password;
import com.revpay.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements IPasswordService {

    private final IPasswordRepo iPasswordRepo;

    @Override
    public void addPassword(String passkey, User user) {
        iPasswordRepo.save(new Password(passkey, user));
    }
}
