package com.rev.app.repository;

import com.rev.app.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Override
    <S extends PaymentMethod> S save(S entity);

    @Override
    Optional<PaymentMethod> findById(Long id);

    @Override
    List<PaymentMethod> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(PaymentMethod entity);

    List<PaymentMethod> findByUserId(Long userId);
}
