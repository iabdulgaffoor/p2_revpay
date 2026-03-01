package com.revpay.domain.repository;

import com.revpay.domain.model.Role;
import java.util.Optional;

public interface IRoleRepo {
    Optional<Role> findByName(String name);

    Role save(Role role);
}
