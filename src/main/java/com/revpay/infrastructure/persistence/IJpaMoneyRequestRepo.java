package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.MoneyRequest;
import com.revpay.domain.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IJpaMoneyRequestRepo extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByRequesterId(Long requesterId);

    List<MoneyRequest> findByRequestedFromId(Long requestedFromId);

    List<MoneyRequest> findByRequestedFromIdAndStatus(Long requestedFromId, TransactionStatus status);
}
