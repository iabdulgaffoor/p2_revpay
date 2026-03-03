package com.rev.app.repository;

import com.rev.app.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {

    @Override
    <S extends Invoice> S save(S entity);

    @Override
    Optional<Invoice> findById(Long id);

    @Override
    List<Invoice> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(Invoice entity);

    List<Invoice> findByBusinessUserId(Long businessUserId);
}
