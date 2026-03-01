package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IJpaRoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
