package com.revpay.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private String customerEmail;
    private String businessName;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private String status; // PENDING, PAID, CANCELLED
    private List<InvoiceItemDto> items = new java.util.ArrayList<>();
}
