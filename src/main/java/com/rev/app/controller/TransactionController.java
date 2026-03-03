package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    @GetMapping("/history")
    public String showTransactionHistory() {
        return "transactions";
    }

    @GetMapping("/send")
    public String showSendMoneyPage() {
        return "send_money"; // e.g. send_money.html
    }

    @GetMapping("/requests")
    public String showMoneyRequestsPage() {
        return "money_requests";
    }
}
