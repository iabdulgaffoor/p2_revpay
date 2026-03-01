package com.revpay.domain.repository;

import com.revpay.domain.model.Invoice;
import java.util.List;
import java.util.Optional;

public interface IInvoiceRepo {
    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    List<Invoice> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    List<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
