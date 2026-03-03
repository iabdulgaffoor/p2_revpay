package com.rev.app.rest;

import com.rev.app.dto.InvoiceDTO;
import com.rev.app.entity.Invoice.InvoiceStatus;
import com.rev.app.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {

    private final IInvoiceService invoiceService;

    @Autowired
    public InvoiceRestController(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/business/{businessUserId}")
    public ResponseEntity<InvoiceDTO> createInvoice(@PathVariable Long businessUserId, @RequestBody InvoiceDTO invoiceDTO) {
        return new ResponseEntity<>(invoiceService.createInvoice(businessUserId, invoiceDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/business/{businessUserId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByBusinessUser(@PathVariable Long businessUserId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByBusinessUserId(businessUserId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(@PathVariable Long id, @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @PostMapping("/{id}/send-notification")
    public ResponseEntity<Void> sendInvoiceNotification(@PathVariable Long id) {
        invoiceService.sendInvoiceNotification(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<InvoiceDTO> markInvoiceAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markInvoiceAsPaid(id));
    }
}
