package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/money-request")
public class MoneyRequestController {

    @GetMapping("/manage")
    public String showMoneyRequestsPage() {
        return "money_requests";
    }
}
