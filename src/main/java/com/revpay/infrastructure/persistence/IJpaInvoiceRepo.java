package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IJpaInvoiceRepo extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    List<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
