package com.rev.app.dto;

import com.rev.app.entity.Invoice.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private Long businessUserId;
    private String businessUserName;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private InvoiceStatus status;
    private String paymentTerms;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private List<InvoiceItemDTO> items;

    public BigDecimal getTotalAmount() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()))
                        .add(item.getTax() != null ? item.getTax() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
