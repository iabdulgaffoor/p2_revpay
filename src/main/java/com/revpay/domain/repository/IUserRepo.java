package com.revpay.domain.repository;

import com.revpay.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface IUserRepo {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findById(Long id);

    List<User> findAll();

    List<User> findByRole(String roleName);

    Optional<User> findByIdentifier(String identifier);

    User save(User user);
}
