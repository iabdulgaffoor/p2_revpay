package com.revpay.interfaces.controller;

import com.revpay.application.dto.SendMoneyRequestDto;
import com.revpay.application.service.ITransactionService;
import com.revpay.domain.model.User;
import com.revpay.domain.repository.IUserRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final ITransactionService transactionService;
    private final IUserRepo userRepo;

    @PostMapping("/send")
    public ResponseEntity<String> sendMoney(@Valid @RequestBody SendMoneyRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User sender = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        transactionService.sendMoney(sender.getId(), request);
        return ResponseEntity.ok("Transaction completed successfully");
    }
}
