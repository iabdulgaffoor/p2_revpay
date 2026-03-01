package com.revpay.infrastructure.repository;

import com.revpay.domain.model.MoneyRequest;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.repository.IMoneyRequestRepo;
import com.revpay.infrastructure.persistence.IJpaMoneyRequestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MoneyRequestRepoImpl implements IMoneyRequestRepo {
    private final IJpaMoneyRequestRepo jpaRepo;

    @Override
    public MoneyRequest save(MoneyRequest request) {
        return jpaRepo.save(request);
    }

    @Override
    public Optional<MoneyRequest> findById(Long id) {
        return jpaRepo.findById(id);
    }

    @Override
    public List<MoneyRequest> findByRequesterId(Long requesterId) {
        return jpaRepo.findByRequesterId(requesterId);
    }

    @Override
    public List<MoneyRequest> findByRequestedFromId(Long requestedFromId) {
        return jpaRepo.findByRequestedFromId(requestedFromId);
    }

    @Override
    public List<MoneyRequest> findByRequestedFromIdAndStatus(Long requestedFromId, TransactionStatus status) {
        return jpaRepo.findByRequestedFromIdAndStatus(requestedFromId, status);
    }
}
