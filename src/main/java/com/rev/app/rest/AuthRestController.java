package com.rev.app.rest;

import com.rev.app.dto.UserDTO;
import com.rev.app.security.JwtProvider;
import com.rev.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final com.rev.app.service.IEmailService emailService;
    private final com.rev.app.repository.IUserRepository userRepository;

    @Autowired
    public AuthRestController(IUserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider, com.rev.app.service.IEmailService emailService, com.rev.app.repository.IUserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register/personal")
    public ResponseEntity<UserDTO> registerPersonal(@Valid @RequestBody RegisterRequest request) {
        UserDTO registeredUser = userService.registerUser(request.getUser(), request.getPassword());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/register/business")
    public ResponseEntity<UserDTO> registerBusiness(@Valid @RequestBody RegisterRequest request) {
        UserDTO registeredUser = userService.registerBusinessUser(request.getUser(), request.getPassword());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Your account has been deactivated by an administrator."));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication failed."));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        com.rev.app.entity.User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new com.rev.app.exception.ResourceNotFoundException("User not found"));
        
        user.setTwoFactorOtp(otp);
        user.setTwoFactorOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        
        // Send Email
        emailService.sendOtpEmail(user.getEmail(), otp);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent to your email. Please verify to continue.");
        response.put("requiresOtp", true);
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        com.rev.app.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.rev.app.exception.ResourceNotFoundException("User not found"));

        if (user.getTwoFactorOtp() == null || !user.getTwoFactorOtp().equals(request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid OTP"));
        }

        if (user.getTwoFactorOtpExpiry() == null || user.getTwoFactorOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "OTP expired"));
        }

        // OTP is valid, clear it
        user.setTwoFactorOtp(null);
        user.setTwoFactorOtpExpiry(null);
        userRepository.save(user);

    // OTP is valid, clear it
        user.setTwoFactorOtp(null);
        user.setTwoFactorOtpExpiry(null);
        userRepository.save(user);

        // Generate JWT
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, java.util.Collections.emptyList());
        // Getting UserDetails requires loading user, or we can just use the email
        // Wait, JwtProvider expects UserDetails.
        org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) new org.springframework.security.core.userdetails.User(user.getEmail(), "", java.util.Collections.emptyList());
        String jwt = jwtProvider.generateToken(userDetails);

        UserDTO userDTO = userService.getUserByEmail(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", userDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest request) {
        com.rev.app.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.rev.app.exception.ResourceNotFoundException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setTwoFactorOtp(otp);
        user.setTwoFactorOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok(Map.of("message", "A new OTP has been sent to your email."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        com.rev.app.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.rev.app.exception.ResourceNotFoundException("If the email is registered, you will receive an OTP."));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setTwoFactorOtp(otp);
        user.setTwoFactorOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok(Map.of("message", "An OTP has been sent to your email to reset your password."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        com.rev.app.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.rev.app.exception.ResourceNotFoundException("User not found"));

        if (user.getTwoFactorOtp() == null || !user.getTwoFactorOtp().equals(request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid OTP"));
        }

        if (user.getTwoFactorOtpExpiry() == null || user.getTwoFactorOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "OTP expired"));
        }

        // OTP is valid, clear it
        user.setTwoFactorOtp(null);
        user.setTwoFactorOtpExpiry(null);

        org.springframework.security.crypto.password.PasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getNewPassword()));
        
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset. You can now login."));
    }

    // Inner classes for Request Bodies
    public static class RegisterRequest {
        private UserDTO user;
        private String password;

        public UserDTO getUser() { return user; }
        public void setUser(UserDTO user) { this.user = user; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class VerifyOtpRequest {
        private String email;
        private String otp;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class ResendOtpRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String email;
        private String otp;
        private String newPassword;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
