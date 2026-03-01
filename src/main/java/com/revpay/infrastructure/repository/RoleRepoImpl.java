package com.revpay.infrastructure.repository;

import com.revpay.domain.model.Role;
import com.revpay.domain.repository.IRoleRepo;
import com.revpay.infrastructure.persistence.IJpaRoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepoImpl implements IRoleRepo {
    private final IJpaRoleRepo jpaRoleRepo;

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepo.findByName(name);
    }

    @Override
    public Role save(Role role) {
        return jpaRoleRepo.save(role);
    }
}
