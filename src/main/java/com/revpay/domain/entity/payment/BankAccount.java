package com.revpay.domain.entity.payment;

import com.revpay.domain.enums.BankName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_bank_account",
		uniqueConstraints = @UniqueConstraint(columnNames = "accountNumber")
		)
public class BankAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String accountNumber;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BankName bankName;
	
	@Column(nullable = false)
	private String ifscCode;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_payment_method_id", nullable = false)
	private PaymentMethod paymentMethod;
	
}
