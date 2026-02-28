package com.revpay.auth.service;

import com.revpay.domain.entity.user.User;
import org.springframework.stereotype.Service;

@Service
public interface IPasswordService {
    void addPassword(String passkey, User user);
}
