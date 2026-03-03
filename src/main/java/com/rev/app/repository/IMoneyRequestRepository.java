package com.rev.app.repository;

import com.rev.app.entity.MoneyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    @Override
    <S extends MoneyRequest> S save(S entity);

    @Override
    Optional<MoneyRequest> findById(Long id);

    @Override
    List<MoneyRequest> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(MoneyRequest entity);

    List<MoneyRequest> findByRequesterId(Long requesterId);

    List<MoneyRequest> findByRequesteeId(Long requesteeId);
}
