package com.revpay.infrastructure.repository;

import com.revpay.domain.model.Invoice;
import com.revpay.domain.repository.IInvoiceRepo;
import com.revpay.infrastructure.persistence.IJpaInvoiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BusinessInvoiceRepoImpl implements IInvoiceRepo {
    private final IJpaInvoiceRepo jpaRepo;

    @Override
    public Invoice save(Invoice invoice) {
        return jpaRepo.save(invoice);
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return jpaRepo.findById(id);
    }

    @Override
    public List<Invoice> findByBusinessIdOrderByCreatedAtDesc(Long businessId) {
        return jpaRepo.findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    @Override
    public List<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId) {
        return jpaRepo.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return jpaRepo.findByInvoiceNumber(invoiceNumber);
    }
}
