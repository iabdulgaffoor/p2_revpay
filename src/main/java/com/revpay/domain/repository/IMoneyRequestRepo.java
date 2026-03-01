package com.revpay.domain.repository;

import com.revpay.domain.model.MoneyRequest;
import com.revpay.domain.model.enums.TransactionStatus;
import java.util.List;
import java.util.Optional;

public interface IMoneyRequestRepo {
    MoneyRequest save(MoneyRequest request);

    Optional<MoneyRequest> findById(Long id);

    List<MoneyRequest> findByRequesterId(Long requesterId);

    List<MoneyRequest> findByRequestedFromId(Long requestedFromId);

    List<MoneyRequest> findByRequestedFromIdAndStatus(Long requestedFromId, TransactionStatus status);
}
