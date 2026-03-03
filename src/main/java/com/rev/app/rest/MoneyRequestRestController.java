package com.rev.app.rest;

import com.rev.app.dto.MoneyRequestDTO;
import com.rev.app.service.IMoneyRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/money-requests")
public class MoneyRequestRestController {

    private final IMoneyRequestService moneyRequestService;

    @Autowired
    public MoneyRequestRestController(IMoneyRequestService moneyRequestService) {
        this.moneyRequestService = moneyRequestService;
    }

    @PostMapping("/send")
    public ResponseEntity<MoneyRequestDTO> sendRequest(@Valid @RequestBody SendMoneyReq request) {
        MoneyRequestDTO createdRequest = moneyRequestService.sendRequest(
                request.getRequesterId(),
                request.getRequesteeId(),
                request.getAmount(),
                request.getPurpose());
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyRequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(moneyRequestService.getRequestById(id));
    }

    @GetMapping("/user/{userId}/incoming")
    public ResponseEntity<List<MoneyRequestDTO>> getIncomingRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(moneyRequestService.getIncomingRequests(userId));
    }

    @GetMapping("/user/{userId}/outgoing")
    public ResponseEntity<List<MoneyRequestDTO>> getOutgoingRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(moneyRequestService.getOutgoingRequests(userId));
    }

    @PostMapping("/{requestId}/accept/user/{userId}")
    public ResponseEntity<MoneyRequestDTO> acceptRequest(@PathVariable Long requestId, 
                                                         @PathVariable Long userId,
                                                         @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(moneyRequestService.acceptRequest(requestId, userId, request.get("pin")));
    }

    @PostMapping("/{requestId}/decline/user/{userId}")
    public ResponseEntity<MoneyRequestDTO> declineRequest(@PathVariable Long requestId, @PathVariable Long userId) {
        return ResponseEntity.ok(moneyRequestService.declineRequest(requestId, userId));
    }

    @PostMapping("/{requestId}/cancel/user/{userId}")
    public ResponseEntity<MoneyRequestDTO> cancelRequest(@PathVariable Long requestId, @PathVariable Long userId) {
        return ResponseEntity.ok(moneyRequestService.cancelRequest(requestId, userId));
    }

    // Inner DTO specifically for endpoint requests
    public static class SendMoneyReq {
        @NotNull(message = "Requester ID is required")
        private Long requesterId;
        
        @NotNull(message = "Requestee ID is required")
        private Long requesteeId;
        
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        private BigDecimal amount;
        
        @NotBlank(message = "Purpose is required")
        private String purpose;

        // Getters and Setters
        public Long getRequesterId() { return requesterId; }
        public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
        public Long getRequesteeId() { return requesteeId; }
        public void setRequesteeId(Long requesteeId) { this.requesteeId = requesteeId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
}
