package com.revpay.domain.entity.invoice;

import com.revpay.domain.entity.business.BusinessProfile;
import com.revpay.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_customer")
public class Customer {

	@Id
	private Long id;
	
	@Column(nullable = false)
	private String address;
	
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@MapsId
	@JoinColumn(name="revpay_user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "revpay_business_profile_id")
	private BusinessProfile businessProfile;
	
}
