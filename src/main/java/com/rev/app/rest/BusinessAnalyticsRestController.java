package com.rev.app.rest;

import com.rev.app.entity.Invoice.InvoiceStatus;
import com.rev.app.entity.Transaction.TransactionStatus;
import com.rev.app.entity.Transaction.TransactionType;
import com.rev.app.service.IInvoiceService;
import com.rev.app.service.ITransactionService;
import com.rev.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/business")
@Slf4j
public class BusinessAnalyticsRestController {

    private final ITransactionService transactionService;
    private final IInvoiceService invoiceService;
    private final IUserService userService;

    @Autowired
    public BusinessAnalyticsRestController(ITransactionService transactionService, 
                                           IInvoiceService invoiceService,
                                           IUserService userService) {
        this.transactionService = transactionService;
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @GetMapping("/{businessUserId}/summary")
    @Cacheable(value = "analytics", key = "#businessUserId")
    public ResponseEntity<Map<String, Object>> getBusinessSummary(@PathVariable Long businessUserId) {
        // Validate user is business user (omitted details for brevity, assumes valid)
        log.info("Calculating Business Analytics (CACHE MISS) for ID: {}", businessUserId);
        
        Map<String, Object> summary = new HashMap<>();

        // 1. Calculate Total Received
        BigDecimal totalReceived = transactionService.getTransactionsByUserId(businessUserId).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .filter(t -> t.getRecipientId() != null && t.getRecipientId().equals(businessUserId))
                .filter(t -> t.getType() == TransactionType.SEND || t.getType() == TransactionType.PAYMENT || t.getType() == TransactionType.ADD_FUNDS)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Calculate Total Sent
        BigDecimal totalSent = transactionService.getTransactionsByUserId(businessUserId).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .filter(t -> t.getSenderId() != null && t.getSenderId().equals(businessUserId))
                .filter(t -> t.getType() == TransactionType.SEND || t.getType() == TransactionType.PAYMENT || t.getType() == TransactionType.WITHDRAW)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Count Pending Invoices
        long pendingInvoicesCount = invoiceService.getInvoicesByBusinessUserId(businessUserId).stream()
                .filter(i -> i.getStatus() == InvoiceStatus.SENT || i.getStatus() == InvoiceStatus.OVERDUE)
                .count();

        // 4. Calculate Total Outstanding Invoice Amount
        BigDecimal outstandingInvoiceAmount = invoiceService.getInvoicesByBusinessUserId(businessUserId).stream()
                .filter(i -> i.getStatus() == InvoiceStatus.SENT || i.getStatus() == InvoiceStatus.OVERDUE)
                .map(i -> i.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.put("totalReceived", totalReceived);
        summary.put("totalSent", totalSent);
        summary.put("pendingInvoicesCount", pendingInvoicesCount);
        summary.put("outstandingInvoiceAmount", outstandingInvoiceAmount);

        return ResponseEntity.ok(summary);
    }
}
