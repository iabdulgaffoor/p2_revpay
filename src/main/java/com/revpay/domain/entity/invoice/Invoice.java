package com.revpay.domain.entity.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.business.BusinessProfile;
import com.revpay.domain.enums.InvoiceStatus;

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
@Table(name = "revpay_invoice")
public class Invoice extends BaseEntity {

	@Column(nullable = false)
	private BigDecimal totalAmount;
	
	@Column(nullable = false)
	private LocalDate dueDate;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_business_profile_id", nullable = false)
	private BusinessProfile businessProfile;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_customer_id", nullable = false)
	private Customer customer;
	
}
