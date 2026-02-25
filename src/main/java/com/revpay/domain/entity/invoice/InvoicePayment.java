package com.revpay.domain.entity.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.common.exception.wallet.NegativeAmountException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_invoice_payment")
public class InvoicePayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter(value = AccessLevel.NONE)
	@Column(nullable = false)
	private BigDecimal amount;
	
	@Column(nullable = false)
	private LocalDateTime paymentDate;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_invoice_id", nullable = false)
	private Invoice invoice;
	
	public void setAmount(double amount) {
		if (amount < 0) {
			throw new NegativeAmountException("amount must be positive");
		}
		this.amount = this.amount.add(BigDecimal.valueOf(amount));
	}
	
}
