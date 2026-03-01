package com.revpay.infrastructure.repository;

import com.revpay.domain.model.User;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.infrastructure.persistence.IJpaUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepoImpl implements IUserRepo {
    private final IJpaUserRepo jpaUserRepo;

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepo.findByEmail(email);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return jpaUserRepo.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaUserRepo.findByPhone(phone);
    }

    @Override
    public User save(User user) {
        return jpaUserRepo.save(user);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepo.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepo.findById(id);
    }

    @Override
    public List<User> findByRole(String roleName) {
        return jpaUserRepo.findByRoles_Role_Name(roleName);
    }

    @Override
    public Optional<User> findByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }
        String trimmed = identifier.trim();
        Optional<User> user = jpaUserRepo.findByEmailIgnoreCase(trimmed);
        if (user.isEmpty()) {
            user = jpaUserRepo.findByPhone(trimmed);
        }
        return user;
    }
}
