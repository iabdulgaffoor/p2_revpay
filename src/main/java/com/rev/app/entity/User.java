package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "rev_users")
@SQLDelete(sql = "UPDATE rev_users SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {

    public enum Role {
        PERSONAL, BUSINESS, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String securityQuestion;

    @Column(nullable = false)
    private String securityAnswer;

    private String transactionPin;

    private String twoFactorOtp;
    
    private java.time.LocalDateTime twoFactorOtpExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Business specific fields
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private Boolean isBusinessVerified;

    // Account Status
    @Column(nullable = true, columnDefinition = "number(1,0) default 1")
    private Boolean isActive = true;

    // Notification Preferences
    private Boolean transactionAlerts = true;

    private Boolean securityAlerts = true;

    public boolean isTransactionAlerts() {
        return Boolean.TRUE.equals(transactionAlerts);
    }

    public boolean isSecurityAlerts() {
        return Boolean.TRUE.equals(securityAlerts);
    }
}
