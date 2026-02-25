package com.revpay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.domain.entity.payment.PaymentMethod;

public interface IPaymentMethodRepo extends JpaRepository<PaymentMethod, Long>{

}
