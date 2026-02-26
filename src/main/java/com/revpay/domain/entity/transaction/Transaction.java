package com.revpay.domain.entity.transaction;

import java.math.BigDecimal;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.user.User;
import com.revpay.domain.enums.TransactionStatus;
import com.revpay.domain.enums.TransactionType;

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
@Table(name = "revpay_transaction")
public class Transaction extends BaseEntity {
	
	@Column(nullable = false)
	private BigDecimal amount;
	
	private String note;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	
	@Enumerated(EnumType.STRING)
	private TransactionStatus status = TransactionStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;
	
}
