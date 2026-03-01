package com.revpay.domain.model;

import com.revpay.common.domain.model.BaseEntity;
import com.revpay.domain.model.enums.TransactionStatus;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private User business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_mail", nullable = false)
    private String customerMail;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();
}
