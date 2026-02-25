package com.revpay.domain.entity.transaction;

import java.math.BigDecimal;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.user.User;
import com.revpay.domain.enums.MoneyRequestStatus;

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
@Table(name = "revpay_money_request")
public class MoneyRequest extends BaseEntity {
	
	@Column(nullable = false)
	private BigDecimal amount;
	
	private String purpose;
	
	@Enumerated(EnumType.STRING)
	private MoneyRequestStatus status = MoneyRequestStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User requester;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User receiver;
	
}
