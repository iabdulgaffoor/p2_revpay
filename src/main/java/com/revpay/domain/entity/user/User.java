package com.revpay.domain.entity.user;

import java.util.ArrayList;
import java.util.List;

import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.wallet.Wallet;
import com.revpay.domain.enums.AccountType;
import com.revpay.domain.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "revpay_user")
public class User extends BaseEntity {
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Transient
	private String fullName;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false, unique = true)
	private String phone;
	
	@Enumerated(EnumType.STRING)
	private AccountType accountType = AccountType.PERSONAL;
	
	@Enumerated(EnumType.STRING)
	private UserStatus status = UserStatus.ACTIVE;
	
	
	
	//relational fields
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Wallet wallet;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserRole> userRoles = new ArrayList<>();
	

	
	//constructors
	//available for hibernate through reflection, not for developer
	protected User() {}
	
	public User(String firstName, String lastName, String email, String phone) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
	}
	
	
	
	public void addWallet(Wallet wallet) {
		this.wallet = wallet;
		wallet.setUser(this);
	}
	
	public void addRole(Role role) {
		UserRole userRole = new UserRole(this, role);
		this.userRoles.add(userRole);
	}
	
	
	
	//run through life-cycle
	@PostLoad
	public void setFullName() {
		this.fullName = String.join(" ", this.firstName, this.lastName);
	}

}
