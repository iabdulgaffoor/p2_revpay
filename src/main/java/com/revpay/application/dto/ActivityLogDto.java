package com.revpay.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ActivityLogDto {
    private String id;
    private String type; // TRANSFER, REQUEST, LOAN
    private String party;
    private BigDecimal amount;
    private LocalDateTime date;
    private String status;
    private boolean isOutgoing;
}
