package com.revpay.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.invoice.Customer;

@Repository
public interface ICustomerRepo extends JpaRepository<Customer, Long> {

}
