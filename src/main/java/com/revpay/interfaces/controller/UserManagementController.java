package com.revpay.interfaces.controller;

import com.revpay.domain.model.User;
import com.revpay.domain.repository.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserManagementController {

    private final IUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/update-password")
    @Transactional
    public String updatePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).orElseThrow();

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/dashboard?error=passwordMismatch";
        }

        if (!passwordEncoder.matches(currentPassword, user.getCredential().getPasswordHash())) {
            return "redirect:/dashboard?error=invalidCurrentPassword";
        }

        user.getCredential().setPasswordHash(passwordEncoder.encode(newPassword));
        user.getCredential().setPasswordLastChanged(LocalDateTime.now());
        userRepo.save(user);

        return "redirect:/dashboard?success=passwordUpdated";
    }

    @PostMapping("/update-pin")
    @Transactional
    public String updatePin(@RequestParam String currentPin,
            @RequestParam String newPin,
            @RequestParam String confirmPin) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).orElseThrow();

        if (!newPin.equals(confirmPin)) {
            return "redirect:/dashboard?error=pinMismatch";
        }

        if (!passwordEncoder.matches(currentPin, user.getTransactionPin().getPinHash())) {
            return "redirect:/dashboard?error=invalidCurrentPin";
        }

        user.getTransactionPin().setPinHash(passwordEncoder.encode(newPin));
        userRepo.save(user);

        return "redirect:/dashboard?success=pinUpdated";
    }
}
