package com.revpay.domain.entity.business;

import com.revpay.domain.entity.user.User;
import com.revpay.domain.enums.BusinessType;
import com.revpay.domain.enums.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="revpay_business_profile",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "taxId")
    	})
public class BusinessProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String businessName;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BusinessType businessType;
	
	@Column(nullable = false)
	private String taxId;
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VerificationStatus verificationStatus = VerificationStatus.PENDING;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false, unique = true)
	private User user;
	
}
