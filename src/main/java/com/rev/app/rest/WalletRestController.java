package com.rev.app.rest;

import com.rev.app.dto.WalletDTO;
import com.rev.app.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
public class WalletRestController {

    private final IWalletService walletService;

    @Autowired
    public WalletRestController(IWalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<WalletDTO> getWalletByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<WalletDTO> addFunds(@PathVariable Long userId, 
                                              @RequestParam BigDecimal amount, 
                                              @RequestParam(required = false) Long paymentMethodId) {
        return ResponseEntity.ok(walletService.addFunds(userId, amount, paymentMethodId));
    }

    @PostMapping("/user/{userId}/withdraw")
    public ResponseEntity<WalletDTO> withdrawFunds(@PathVariable Long userId, 
                                                   @RequestParam BigDecimal amount, 
                                                   @RequestParam(required = false) Long paymentMethodId) {
        return ResponseEntity.ok(walletService.withdrawFunds(userId, amount, paymentMethodId));
    }
}
