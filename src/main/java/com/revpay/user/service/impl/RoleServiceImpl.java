package com.revpay.user.service.impl;

import com.revpay.common.exception.user.RoleNotFoundException;
import com.revpay.domain.entity.user.Role;
import com.revpay.user.repository.IRoleRepo;
import com.revpay.user.service.IRoleSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleSevice {

    private final IRoleRepo iRoleRepo;

    @Override
    public Role getDefaultRole() {
/*
        return iRoleRepo
                .findById(1L)
                .orElseThrow(
                        () -> new RoleNotFoundException("default role fetch failure")
                );

 */
        return new Role("USER_CLIENT");
    }

    public Role saveRole(Role role) {
        return iRoleRepo.save(role);
    }
}
