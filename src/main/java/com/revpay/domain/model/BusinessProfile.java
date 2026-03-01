package com.revpay.domain.model;

import com.revpay.common.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    private String businessType;

    private String taxId;

    private String businessAddress;

    @Builder.Default
    private boolean isVerified = false;
}
