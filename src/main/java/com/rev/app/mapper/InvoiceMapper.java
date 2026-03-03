package com.rev.app.mapper;

import com.rev.app.dto.InvoiceDTO;
import com.rev.app.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    private final InvoiceItemMapper invoiceItemMapper;

    @Autowired
    public InvoiceMapper(InvoiceItemMapper invoiceItemMapper) {
        this.invoiceItemMapper = invoiceItemMapper;
    }

    public InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setCustomerEmail(invoice.getCustomerEmail());
        dto.setCustomerAddress(invoice.getCustomerAddress());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentTerms(invoice.getPaymentTerms());
        dto.setDueDate(invoice.getDueDate());
        dto.setCreatedAt(invoice.getCreatedAt());

        if (invoice.getBusinessUser() != null) {
            dto.setBusinessUserId(invoice.getBusinessUser().getId());
            dto.setBusinessUserName(invoice.getBusinessUser().getBusinessName());
        }

        if (invoice.getItems() != null) {
            dto.setItems(invoice.getItems().stream()
                    .map(invoiceItemMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Invoice toEntity(InvoiceDTO dto) {
        if (dto == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setId(dto.getId());
        invoice.setCustomerName(dto.getCustomerName());
        invoice.setCustomerEmail(dto.getCustomerEmail());
        invoice.setCustomerAddress(dto.getCustomerAddress());
        invoice.setStatus(dto.getStatus());
        invoice.setPaymentTerms(dto.getPaymentTerms());
        invoice.setDueDate(dto.getDueDate());
        invoice.setCreatedAt(dto.getCreatedAt());

        if (dto.getItems() != null) {
            invoice.setItems(dto.getItems().stream()
                    .map(itemDto -> {
                        var item = invoiceItemMapper.toEntity(itemDto);
                        item.setInvoice(invoice);
                        return item;
                    })
                    .collect(Collectors.toList()));
        }

        return invoice;
    }
}
