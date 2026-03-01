package com.revpay.domain.model;

import com.revpay.common.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String cardType; // CREDIT, DEBIT

    @Column(nullable = false)
    private String cardNumber; // Masked or encrypted in production

    @Column(nullable = false)
    private String expiryDate;

    private String cardHolderName;

    @Builder.Default
    private boolean isDefault = false;
}
