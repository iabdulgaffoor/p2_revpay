package com.revpay.domain.model;

import com.revpay.common.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredential extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String passwordHash;

    private String passwordSalt; // Using BCrypt usually doesn't need separate salt, but requested.

    private LocalDateTime passwordLastChanged;

    @Builder.Default
    private int failedLoginAttempts = 0;

    @Builder.Default
    private boolean accountLocked = false;

    private LocalDateTime lastLoginAt;
}
