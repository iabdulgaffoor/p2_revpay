package com.rev.app.service;

import com.rev.app.dto.MoneyRequestDTO;
import java.math.BigDecimal;
import java.util.List;

public interface IMoneyRequestService {
    MoneyRequestDTO sendRequest(Long requesterId, Long requesteeId, BigDecimal amount, String purpose);
    MoneyRequestDTO getRequestById(Long requestId);
    List<MoneyRequestDTO> getIncomingRequests(Long userId);
    List<MoneyRequestDTO> getOutgoingRequests(Long userId);
    MoneyRequestDTO acceptRequest(Long requestId, Long userId, String pin);
    MoneyRequestDTO declineRequest(Long requestId, Long userId);
    MoneyRequestDTO cancelRequest(Long requestId, Long userId);
}
