package com.revpay.user.service.impl;

import com.revpay.domain.entity.user.Role;
import com.revpay.user.repository.IRoleRepo;
import com.revpay.user.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepo iRoleRepo;

    public Role saveRole(Role role)
    {
        return iRoleRepo.save(role);
    }

    @Override
    public Optional<Role> getUserById(Long id) {
        return iRoleRepo.findById(id);
    }
}
