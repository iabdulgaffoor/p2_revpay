package com.revpay.domain.entity.auth;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "revpay_password")
public class Password extends BaseEntity {
	
	@Column(nullable = false)
	private String passkey;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User user;
	
	public void addPassword(String passkey)
	{
		this.passkey = passkey;
	}

	public Password(String passkey, User user) {
		this.passkey = passkey;
		this.user = user;
	}
	
}
