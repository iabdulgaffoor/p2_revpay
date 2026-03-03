package com.rev.app.mapper;

import com.rev.app.dto.InvoiceItemDTO;
import com.rev.app.entity.InvoiceItem;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemMapper {

    public InvoiceItemDTO toDTO(InvoiceItem invoiceItem) {
        if (invoiceItem == null) {
            return null;
        }

        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(invoiceItem.getId());
        dto.setDescription(invoiceItem.getDescription());
        dto.setQuantity(invoiceItem.getQuantity());
        dto.setUnitPrice(invoiceItem.getUnitPrice());
        dto.setTax(invoiceItem.getTax());

        if (invoiceItem.getInvoice() != null) {
            dto.setInvoiceId(invoiceItem.getInvoice().getId());
        }

        return dto;
    }

    public InvoiceItem toEntity(InvoiceItemDTO dto) {
        if (dto == null) {
            return null;
        }

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setId(dto.getId());
        invoiceItem.setDescription(dto.getDescription());
        invoiceItem.setQuantity(dto.getQuantity());
        invoiceItem.setUnitPrice(dto.getUnitPrice());
        invoiceItem.setTax(dto.getTax());

        return invoiceItem;
    }
}
