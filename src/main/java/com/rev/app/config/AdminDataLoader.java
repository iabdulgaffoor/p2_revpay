package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminDataLoader implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminDataLoader(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("shaikgaffoorbhai@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("shaikgaffoorbhai@gmail.com");
            admin.setPhoneNumber("8978072819");
            admin.setPassword(passwordEncoder.encode("Shaik_Gaffoor@2819"));
            admin.setRole(Role.ADMIN);
            admin.setSecurityQuestion("Admin Key?");
            admin.setSecurityAnswer("admin");

            userRepository.save(admin);
            log.info("Default Admin user created successfully (shaikgaffoorbhai@gmail.com / Shaik_Gaffoor@2819)");
        } else {
            log.info("Admin user already exists.");
        }
    }
}
