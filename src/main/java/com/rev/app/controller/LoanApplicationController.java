package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loan")
public class LoanApplicationController {

    @GetMapping("/apply")
    public String showLoanApplicationPage() {
        return "apply_loan";
    }

    @GetMapping("/status")
    public String showLoanStatusPage() {
        return "loan_status";
    }
}
