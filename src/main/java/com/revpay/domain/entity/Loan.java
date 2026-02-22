package com.revpay.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private BigDecimal interestRate;
	private BigDecimal emiAmount;
	private BigDecimal totalAmount;
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	@OneToOne
	private LoanApplication loanApplication;
	
}
