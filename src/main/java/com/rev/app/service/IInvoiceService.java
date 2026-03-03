package com.rev.app.service;

import com.rev.app.dto.InvoiceDTO;
import java.util.List;

public interface IInvoiceService {
    InvoiceDTO createInvoice(Long businessUserId, InvoiceDTO invoiceDTO);
    InvoiceDTO getInvoiceById(Long invoiceId);
    List<InvoiceDTO> getInvoicesByBusinessUserId(Long businessUserId);
    InvoiceDTO updateInvoiceStatus(Long invoiceId, com.rev.app.entity.Invoice.InvoiceStatus status);
    void sendInvoiceNotification(Long invoiceId);
    InvoiceDTO markInvoiceAsPaid(Long invoiceId);
}
