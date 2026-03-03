package com.rev.app.rest;

import com.rev.app.dto.TransactionDTO;
import com.rev.app.entity.Transaction.TransactionType;
import com.rev.app.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import com.rev.app.service.IUserService;
import com.rev.app.dto.UserDTO;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final ITransactionService transactionService;
    private final IUserService userService;

    @Autowired
    public TransactionRestController(ITransactionService transactionService, IUserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<TransactionDTO> sendMoney(@RequestBody SendMoneyRequest request) {
        UserDTO recipient = userService.getUserByIdentifier(request.getRecipientIdentifier());
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient with identifier " + request.getRecipientIdentifier() + " not found");
        }

        TransactionDTO transaction = transactionService.sendMoney(
                request.getSenderId(), 
                recipient.getId(), 
                request.getAmount(), 
                request.getDescription(), 
                request.getTransactionPin());
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<TransactionDTO>> filterTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String status) {
        
        return ResponseEntity.ok(transactionService.filterTransactions(
                userId, type, startDate, endDate, minAmount, maxAmount, status));
    }

    @GetMapping(value = "/user/{userId}/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportTransactionsToCsv(@PathVariable Long userId) {
        byte[] csvData = transactionService.exportTransactionsToCSV(userId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transactions.csv")
                .body(csvData);
    }

    // Inner DTO specifically for endpoint requests
    public static class SendMoneyRequest {
        private Long senderId;
        private String recipientIdentifier;
        private BigDecimal amount;
        private String description;
        private String transactionPin;

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getRecipientIdentifier() { return recipientIdentifier; }
        public void setRecipientIdentifier(String recipientIdentifier) { this.recipientIdentifier = recipientIdentifier; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTransactionPin() { return transactionPin; }
        public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }
    }
}
