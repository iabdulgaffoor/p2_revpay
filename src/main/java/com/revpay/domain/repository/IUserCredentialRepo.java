package com.revpay.domain.repository;

import com.revpay.domain.model.UserCredential;
import java.util.Optional;

public interface IUserCredentialRepo {
    Optional<UserCredential> findByUserId(Long userId);

    UserCredential save(UserCredential credential);
}
