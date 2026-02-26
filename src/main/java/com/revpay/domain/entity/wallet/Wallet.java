package com.revpay.domain.entity.wallet;

import java.math.BigDecimal;

import com.revpay.common.exception.wallet.NegativeAmountException;
import com.revpay.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_wallet")
public class Wallet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Setter(AccessLevel.NONE)
	private String walUnqId;
	
	@Column(nullable = false)
	@Setter(AccessLevel.NONE)
	private BigDecimal balance = BigDecimal.ZERO;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User user;

	protected Wallet() {}

	public Wallet(String walUnqId, double balance) {
		this.walUnqId = walUnqId;
		this.balance = this.balance.add(BigDecimal.valueOf(balance));
	}

	public void withdraw(double amount) {
		if (amount < 0) {
			throw new NegativeAmountException("Amount must be positive");
		}
		this.balance = this.balance.subtract(BigDecimal.valueOf(amount));
	}
	
	public void deposit(double amount) {
		if (amount < 0) {
			throw new NegativeAmountException("Amount must be positive");
		}
		this.balance = this.balance.add(BigDecimal.valueOf(amount));
	}
	
}
