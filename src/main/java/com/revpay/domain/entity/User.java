package com.revpay.domain.entity;

import java.time.LocalDateTime;

import com.revpay.domain.enums.AccountType;
import com.revpay.domain.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fullName;
	private String email;
	private String phone;
	private String password;
	
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Wallet wallet;
	
}
