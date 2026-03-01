package com.revpay.application.service;

import com.revpay.application.dto.InvoiceDto;
import com.revpay.application.dto.InvoiceItemDto;
import com.revpay.application.dto.MoneyRequestDto;
import com.revpay.common.exception.ResourceNotFoundException;
import com.revpay.common.exception.UnauthorizedException;
import com.revpay.domain.model.Invoice;
import com.revpay.domain.model.InvoiceItem;
import com.revpay.domain.model.User;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.model.enums.UserStatus;
import com.revpay.domain.repository.IInvoiceRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements IInvoiceService {

    private final IInvoiceRepo invoiceRepo;
    private final IUserRepo userRepo;
    private final IMoneyRequestService moneyRequestService;

    @Override
    @Transactional
    public InvoiceDto createInvoice(Long businessId, InvoiceDto invoiceDto) {
        User business = userRepo.findById(businessId).orElseThrow();

        if (business.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("Business account is " + business.getStatus(), "ACCOUNT_SUSPENDED");
        }

        String customerEmail = invoiceDto.getCustomerEmail() != null ? invoiceDto.getCustomerEmail().trim() : "";
        User customer = userRepo.findByEmailIgnoreCase(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerEmail));

        if (customer.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("Customer account is " + customer.getStatus(), "ACCOUNT_SUSPENDED");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .business(business)
                .customer(customer)
                .customerEmail(invoiceDto.getCustomerEmail())
                .customerMail(invoiceDto.getCustomerEmail())
                .customerName(customer.getFullName())
                .totalAmount(invoiceDto.getTotalAmount())
                .dueDate(invoiceDto.getDueDate())
                .status(TransactionStatus.PENDING)
                .build();

        if (invoiceDto.getItems() != null) {
            List<InvoiceItem> items = invoiceDto.getItems().stream().map(itemDto -> {
                BigDecimal total = itemDto.getTotal();
                if (total == null && itemDto.getUnitPrice() != null && itemDto.getQuantity() != null) {
                    total = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                }
                return InvoiceItem.builder()
                        .invoice(invoice)
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .total(total)
                        .build();
            }).collect(Collectors.toList());
            invoice.setItems(items);
        }

        Invoice saved = invoiceRepo.save(invoice);

        // Automatically generate a MoneyRequest for the customer
        MoneyRequestDto requestDto = new MoneyRequestDto();
        requestDto.setRequestedFromEmail(customerEmail);
        requestDto.setAmount(saved.getTotalAmount());
        requestDto.setPurpose("Invoice Payment: " + saved.getInvoiceNumber());
        moneyRequestService.sendRequest(business.getId(), requestDto);

        return mapToDto(saved);
    }

    @Override
    public List<InvoiceDto> getBusinessInvoices(Long businessId) {
        return invoiceRepo.findByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getCustomerInvoices(Long customerId) {
        return invoiceRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void payInvoice(Long customerId, String invoiceNumber, String pin) {
        // Logic to pay invoice (could bridge to moneyRequestService or
        // transactionService)
    }

    @Override
    @Transactional
    public void cancelInvoice(Long businessId, String invoiceNumber) {
        Invoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber).orElseThrow();
        if (!invoice.getBusiness().getId().equals(businessId)) {
            throw new UnauthorizedException("Not authorized to cancel this invoice");
        }
        invoice.setStatus(TransactionStatus.FAILED);
        invoiceRepo.save(invoice);
    }

    private InvoiceDto mapToDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setCustomerEmail(invoice.getCustomer().getEmail());
        dto.setBusinessName(invoice.getBusiness().getFullName());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setDueDate(invoice.getDueDate());
        dto.setStatus(invoice.getStatus().name());
        if (invoice.getItems() != null) {
            dto.setItems(invoice.getItems().stream().map(item -> {
                InvoiceItemDto itemDto = new InvoiceItemDto();
                itemDto.setDescription(item.getDescription());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setTotal(item.getTotal());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
