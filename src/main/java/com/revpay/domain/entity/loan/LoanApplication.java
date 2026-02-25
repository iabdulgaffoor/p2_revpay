package com.revpay.domain.entity.loan;

import java.math.BigDecimal;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.business.BusinessProfile;
import com.revpay.domain.enums.LoanStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_loan_application")
public class LoanApplication extends BaseEntity {
	
	@Column(nullable = false)
	private BigDecimal amount;
	
	@Column(nullable = false)
	private String purpose;
	
	@Column(nullable = false)
	private Integer tenureMonths;
	
	@Enumerated(EnumType.STRING)
	private LoanStatus status = LoanStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_business_profile_id", nullable = false)
	private BusinessProfile businessProfile;
	
}
