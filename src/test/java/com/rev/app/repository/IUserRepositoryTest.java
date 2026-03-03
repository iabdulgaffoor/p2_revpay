package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_ReturnsUser() {
        User user = new User();
        user.setEmail("repo@test.com");
        user.setFullName("Repo Test");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("repo@test.com");

        assertTrue(found.isPresent());
        assertEquals("repo@test.com", found.get().getEmail());
    }

    @Test
    void findByPhoneNumber_ReturnsUser() {
        User user = new User();
        user.setEmail("repo2@test.com");
        user.setPhoneNumber("1234567890");
        user.setFullName("Repo Test 2");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByPhoneNumber("1234567890");

        assertTrue(found.isPresent());
        assertEquals("1234567890", found.get().getPhoneNumber());
    }

    @Test
    void findByFullNameIgnoreCase_ReturnsUser() {
        User user = new User();
        user.setEmail("repo3@test.com");
        user.setFullName("JEAN LUC");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByFullNameIgnoreCase("jean luc");

        assertTrue(found.isPresent());
        assertEquals("JEAN LUC", found.get().getFullName());
    }
}
