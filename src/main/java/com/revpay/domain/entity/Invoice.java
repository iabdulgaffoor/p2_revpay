package com.revpay.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.revpay.domain.enums.InvoiceStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private BigDecimal totalAmount;
	private LocalDate dueDate;
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	private BusinessProfile businessProfile;
	
	@ManyToOne
	private Customer customer;
	
}
