package com.revpay.domain.entity.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_card_details")
public class CardDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String cardNumber;
	
	@Column(nullable = false)
	private String expiryDate;
	
	@Column(nullable = false)
	private String cvv;
	
	@Column(nullable = false)
	private String billingAddress;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_payment_method_id", nullable = false)
	private PaymentMethod paymentMethod;
	
}
