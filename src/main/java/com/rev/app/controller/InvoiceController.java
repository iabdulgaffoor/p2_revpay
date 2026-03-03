package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @GetMapping("/manage")
    public String showInvoicesPage() {
        return "invoices";
    }

    @GetMapping("/create")
    public String showCreateInvoicePage() {
        return "create_invoice";
    }
}
