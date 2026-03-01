package com.revpay.infrastructure.security;

import com.revpay.domain.model.User;
import com.revpay.domain.model.enums.UserStatus;
import com.revpay.domain.repository.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final IUserRepo userRepo;

        @Override
        @Transactional(readOnly = true)
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userRepo.findByEmailIgnoreCase(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with email: " + email));

                boolean enabled = user.getStatus() != UserStatus.SUSPENDED && !user.isDeleted();
                boolean accountNonLocked = user.getStatus() != UserStatus.LOCKED
                                && !user.getCredential().isAccountLocked();

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getCredential().getPasswordHash())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority(
                                                                "ROLE_" + role.getRole().getName()))
                                                .collect(Collectors.toList()))
                                .accountLocked(!accountNonLocked)
                                .disabled(!enabled)
                                .build();
        }
}
