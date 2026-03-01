package com.revpay.application.service;

import com.revpay.application.dto.AuthResponseDto;
import com.revpay.application.dto.LoginRequestDto;
import com.revpay.application.dto.RegisterRequestDto;
import com.revpay.common.exception.AuthenticationException;
import com.revpay.common.exception.BusinessException;
import com.revpay.domain.model.*;
import com.revpay.domain.model.enums.UserStatus;
import com.revpay.domain.repository.IRoleRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.infrastructure.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

        private final IUserRepo userRepo;
        private final IRoleRepo roleRepo;
        private final PasswordEncoder passwordEncoder;
        private final IJwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        @Override
        @Transactional
        public AuthResponseDto register(RegisterRequestDto request) {
                String email = request.getEmail() != null ? request.getEmail().trim() : "";
                log.info("Registering new user with email: {}", email);
                if (userRepo.findByEmailIgnoreCase(email).isPresent()) {
                        log.warn("Registration failed: Email {} already exists", email);
                        throw new BusinessException("Email already registered", "EMAIL_EXISTS");
                }

                User user = User.builder()
                                .email(email)
                                .phone(request.getPhone())
                                .fullName(request.getFullName())
                                .accountType(request.getAccountType())
                                .status(UserStatus.ACTIVE)
                                .build();

                UserCredential credential = UserCredential.builder()
                                .user(user)
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .passwordLastChanged(LocalDateTime.now())
                                .build();

                user.setCredential(credential);

                Wallet wallet = Wallet.builder()
                                .user(user)
                                .balance(new java.math.BigDecimal("1000.00"))
                                .currency("INR")
                                .build();

                user.setWallet(wallet);

                TransactionPin transactionPin = TransactionPin.builder()
                                .user(user)
                                .pinHash(passwordEncoder.encode(request.getTransactionPin()))
                                .build();

                user.setTransactionPin(transactionPin);

                String roleName = request.getAccountType().name();
                Role userRole = roleRepo.findByName(roleName)
                                .orElseGet(() -> roleRepo.save(Role.builder().name(roleName).build()));
                UserRole assignment = UserRole.builder()
                                .user(user)
                                .role(userRole)
                                .assignedAt(LocalDateTime.now())
                                .active(true)
                                .build();
                user.getRoles().add(assignment);

                userRepo.save(user);

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(credential.getPasswordHash())
                                .authorities("ROLE_" + roleName)
                                .build();

                String jwtToken = jwtUtils.generateToken(userDetails);
                return AuthResponseDto.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public AuthResponseDto login(LoginRequestDto request) {
                String email = request.getEmail() != null ? request.getEmail().trim() : "";
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(email,
                                                        request.getPassword()));
                } catch (org.springframework.security.core.AuthenticationException e) {
                        throw new AuthenticationException("Invalid email or password");
                }

                User user = userRepo.findByEmailIgnoreCase(email)
                                .orElseThrow(() -> new AuthenticationException("User not found"));

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getCredential().getPasswordHash())
                                .authorities(user.getRoles().stream()
                                                .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                                "ROLE_" + r.getRole().getName()))
                                                .collect(java.util.stream.Collectors.toList()))
                                .build();

                String jwtToken = jwtUtils.generateToken(userDetails);
                return AuthResponseDto.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .build();
        }
}
