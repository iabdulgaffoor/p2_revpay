package com.revpay.domain.entity.user;


import com.revpay.domain.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_transaction_pin")
public class TransactionPin extends BaseEntity {
	
	private String pinHash;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User user;

	public TransactionPin(String pinHash, User user) {
		this.pinHash = pinHash;
		this.user = user;
	}
	
}
