package com.rev.app.dto;

import com.rev.app.entity.MoneyRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRequestDTO {
    private Long id;
    
    @NotNull(message = "Requester ID is required")
    private Long requesterId;
    private String requesterName;
    
    @NotNull(message = "Requestee ID is required")
    private Long requesteeId;
    private String requesteeName;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    @NotBlank(message = "Purpose is required")
    private String purpose;
    
    private RequestStatus status;
    private LocalDateTime createdAt;
}
