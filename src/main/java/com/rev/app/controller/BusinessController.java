package com.rev.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/business")
public class BusinessController {

    @GetMapping("/analytics")
    public String showAnalyticsPage() {
        return "business-dashboard";
    }
}
