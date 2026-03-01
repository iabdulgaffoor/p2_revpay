package com.revpay.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReport {
    private BigDecimal totalReceived;
    private BigDecimal totalSent;
    private BigDecimal pendingAmount;
    private long totalTransactions;
    private Map<String, BigDecimal> revenueByMonth;
    private BigDecimal outstandingInvoices;
}
