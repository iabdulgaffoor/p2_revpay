package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String showDashboard() {
        return "dashboard"; // Assuming dashboard.html
    }

    @GetMapping("/business")
    public String showBusinessDashboard() {
        return "business-dashboard";
    }

    @GetMapping("/transactions")
    public String showTransactions() {
        return "transactions";
    }

    @GetMapping("/wallet")
    public String showWallet() {
        return "wallet";
    }

}
