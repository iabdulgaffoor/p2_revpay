package com.rev.app.service;

import com.rev.app.dto.InvoiceDTO;
import com.rev.app.entity.Invoice;
import com.rev.app.entity.Invoice.InvoiceStatus;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.InvoiceMapper;
import com.rev.app.repository.IInvoiceRepository;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IInvoiceServiceImpl implements IInvoiceService {

    private final IInvoiceRepository invoiceRepository;
    private final IUserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final INotificationService notificationService; // Added to send notifications

    @Autowired
    public IInvoiceServiceImpl(IInvoiceRepository invoiceRepository, 
                               IUserRepository userRepository, 
                               InvoiceMapper invoiceMapper,
                               INotificationService notificationService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.invoiceMapper = invoiceMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public InvoiceDTO createInvoice(Long businessUserId, InvoiceDTO invoiceDTO) {
        User businessUser = userRepository.findById(businessUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Business user not found"));

        if (businessUser.getRole() != User.Role.BUSINESS) {
            throw new BadRequestException("User does not have a business account");
        }

        Invoice invoice = invoiceMapper.toEntity(invoiceDTO);
        invoice.setBusinessUser(businessUser);
        invoice.setCreatedAt(java.time.LocalDateTime.now()); // Ensure NOT NULL constraint is met
        
        // If status wasn't provided, default to DRAFT
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.DRAFT);
        }
        
        // Ensure bidirectional relationship is set for cascading
        if (invoice.getItems() != null) {
            invoice.getItems().forEach(item -> item.setInvoice(invoice));
        }

        return invoiceMapper.toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDTO getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(invoiceMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
    }

    @Override
    public List<InvoiceDTO> getInvoicesByBusinessUserId(Long businessUserId) {
        return invoiceRepository.findByBusinessUserId(businessUserId).stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long invoiceId, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
                
        invoice.setStatus(status);
        return invoiceMapper.toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public void sendInvoiceNotification(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        // E.g., find user by email
        userRepository.findByEmail(invoice.getCustomerEmail()).ifPresent(customer -> {
            notificationService.createNotification(customer.getId(), 
                "You have a new invoice from " + invoice.getBusinessUser().getBusinessName(), 
                com.rev.app.entity.Notification.NotificationType.INVOICE);
        });
        
        // Change status to sent if DRAFT
        if (invoice.getStatus() == InvoiceStatus.DRAFT) {
            updateInvoiceStatus(invoiceId, InvoiceStatus.SENT);
        }
    }

    @Override
    @Transactional
    public InvoiceDTO markInvoiceAsPaid(Long invoiceId) {
        return updateInvoiceStatus(invoiceId, InvoiceStatus.PAID);
    }
}
