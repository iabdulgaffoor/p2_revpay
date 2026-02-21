package com.revpay.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.domain.enums.TransactionStatus;
import com.revpay.domain.enums.TransactionType;

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
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private BigDecimal amount;
	private String note;
	
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	private User sender;
	
	@ManyToOne
	private User receiver;
	
}
