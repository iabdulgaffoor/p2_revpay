package com.revpay.user.service;

import com.revpay.domain.entity.user.Role;

import java.util.Optional;

public interface IRoleService {
    Role saveRole(Role role);
    Optional<Role> getUserById(Long id);
}
