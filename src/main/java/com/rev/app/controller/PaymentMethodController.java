package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment-method")
public class PaymentMethodController {

    @GetMapping("/manage")
    public String showPaymentMethodsPage() {
        return "payment_methods";
    }
}
