package com.rev.app.repository;

import com.rev.app.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    @Override
    <S extends InvoiceItem> S save(S entity);

    @Override
    Optional<InvoiceItem> findById(Long id);

    @Override
    List<InvoiceItem> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(InvoiceItem entity);

    List<InvoiceItem> findByInvoiceId(Long invoiceId);
}
