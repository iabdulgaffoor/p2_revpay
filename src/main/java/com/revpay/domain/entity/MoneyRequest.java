package com.revpay.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.domain.enums.MoneyRequestStatus;

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
public class MoneyRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private BigDecimal amount;
	private String purpose;
	
	@Enumerated(EnumType.STRING)
	private MoneyRequestStatus status;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	private User requester;
	
	@ManyToOne
	private User receiver;
	
}
