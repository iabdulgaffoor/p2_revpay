package com.revpay.domain.entity.loan;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_loan")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private BigDecimal interestRate;
	
	@Column(nullable = false)
	private BigDecimal emiAmount;
	
	@Column(nullable = false)
	private BigDecimal totalAmount;
	
	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_loan_application_id", nullable = false)
	private LoanApplication loanApplication;
	
}
