package com.rev.app.rest;

import com.rev.app.dto.PaymentMethodDTO;
import com.rev.app.service.IPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodRestController {

    private final IPaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodRestController(IPaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(@PathVariable Long userId, @Valid @RequestBody PaymentMethodDTO pmDTO) {
        return new ResponseEntity<>(paymentMethodService.addPaymentMethod(userId, pmDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody PaymentMethodDTO pmDTO) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, pmDTO));
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id, @PathVariable Long userId) {
        paymentMethodService.deletePaymentMethod(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default/user/{userId}")
    public ResponseEntity<PaymentMethodDTO> setDefaultPaymentMethod(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(paymentMethodService.setDefaultPaymentMethod(id, userId));
    }
}
