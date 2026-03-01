package com.revpay.application.service;

import com.revpay.application.dto.InvoiceDto;
import java.util.List;

public interface IInvoiceService {
    InvoiceDto createInvoice(Long businessId, InvoiceDto invoiceDto);

    List<InvoiceDto> getBusinessInvoices(Long businessId);

    List<InvoiceDto> getCustomerInvoices(Long customerId);

    void payInvoice(Long customerId, String invoiceNumber, String pin);

    void cancelInvoice(Long businessId, String invoiceNumber);
}
