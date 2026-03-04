package com.rev.app.mapper;

import com.rev.app.dto.MoneyRequestDTO;
import com.rev.app.entity.MoneyRequest;
import org.springframework.stereotype.Component;

@Component
public class MoneyRequestMapper {

    public MoneyRequestDTO toDTO(MoneyRequest moneyRequest) {
        if (moneyRequest == null) {
            return null;
        }

        MoneyRequestDTO dto = new MoneyRequestDTO();
        dto.setId(moneyRequest.getId());
        dto.setAmount(moneyRequest.getAmount());
        dto.setPurpose(moneyRequest.getPurpose());
        dto.setStatus(moneyRequest.getStatus());
        dto.setCreatedAt(moneyRequest.getCreatedAt());

        if (moneyRequest.getRequester() != null) {
            dto.setRequesterId(moneyRequest.getRequester().getId());
            dto.setRequesterName(moneyRequest.getRequester().getFullName());
        }

        if (moneyRequest.getRequestee() != null) {
            dto.setRequesteeId(moneyRequest.getRequestee().getId());
            dto.setRequesteeName(moneyRequest.getRequestee().getFullName());
        }

        if (moneyRequest.getInvoice() != null) {
            dto.setInvoiceId(moneyRequest.getInvoice().getId());
        }

        return dto;
    }

    public MoneyRequest toEntity(MoneyRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setId(dto.getId());
        moneyRequest.setAmount(dto.getAmount());
        moneyRequest.setPurpose(dto.getPurpose());
        moneyRequest.setStatus(dto.getStatus());
        moneyRequest.setCreatedAt(dto.getCreatedAt());

        // Note: User objects (Requester/Requestee) must be explicitly set by the
        // Service layer

        return moneyRequest;
    }
}
