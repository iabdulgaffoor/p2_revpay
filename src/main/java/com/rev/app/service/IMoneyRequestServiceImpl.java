package com.rev.app.service;

import com.rev.app.dto.MoneyRequestDTO;
import com.rev.app.entity.MoneyRequest;
import com.rev.app.entity.MoneyRequest.RequestStatus;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.MoneyRequestMapper;
import com.rev.app.repository.IMoneyRequestRepository;
import com.rev.app.repository.IInvoiceRepository;
import com.rev.app.repository.INotificationRepository;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IMoneyRequestServiceImpl implements IMoneyRequestService {

    private final IMoneyRequestRepository moneyRequestRepository;
    private final IUserRepository userRepository;
    private final ITransactionService transactionService;
    private final MoneyRequestMapper moneyRequestMapper;
    private final IInvoiceRepository invoiceRepository;
    private final INotificationService notificationService;
    private final INotificationRepository notificationRepository;

    @Autowired
    public IMoneyRequestServiceImpl(IMoneyRequestRepository moneyRequestRepository,
            IUserRepository userRepository,
            ITransactionService transactionService,
            MoneyRequestMapper moneyRequestMapper,
            IInvoiceRepository invoiceRepository,
            INotificationService notificationService,
            INotificationRepository notificationRepository) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.moneyRequestMapper = moneyRequestMapper;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public MoneyRequestDTO sendRequest(Long requesterId, Long requesteeId, BigDecimal amount, String purpose,
            Long invoiceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
        if (requesterId.equals(requesteeId)) {
            throw new BadRequestException("Cannot request money from yourself");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));
        User requestee = userRepository.findById(requesteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Requestee not found"));

        MoneyRequest request = new MoneyRequest();
        request.setRequester(requester);
        request.setRequestee(requestee);
        request.setAmount(amount);
        request.setPurpose(purpose);
        request.setStatus(RequestStatus.PENDING);

        if (invoiceId != null) {
            invoiceRepository.findById(invoiceId).ifPresent(request::setInvoice);
        }

        MoneyRequest savedRequest = moneyRequestRepository.save(request);

        // Trigger notification for the requestee
        notificationService.createNotification(
                requestee.getId(),
                String.format("%s has requested INR %.2f for %s", requester.getFullName(), amount, purpose),
                com.rev.app.entity.Notification.NotificationType.MONEY_REQUEST,
                savedRequest.getId());

        return moneyRequestMapper.toDTO(savedRequest);
    }

    // Compat method for existing callers
    public MoneyRequestDTO sendRequest(Long requesterId, Long requesteeId, BigDecimal amount, String purpose) {
        return sendRequest(requesterId, requesteeId, amount, purpose, null);
    }

    @Override
    public MoneyRequestDTO getRequestById(Long requestId) {
        return moneyRequestRepository.findById(requestId)
                .map(moneyRequestMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Money request not found"));
    }

    @Override
    public List<MoneyRequestDTO> getIncomingRequests(Long userId) {
        return moneyRequestRepository.findByRequesteeId(userId).stream()
                .map(moneyRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoneyRequestDTO> getOutgoingRequests(Long userId) {
        return moneyRequestRepository.findByRequesterId(userId).stream()
                .map(moneyRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MoneyRequestDTO acceptRequest(Long requestId, Long userId, String pin) {
        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Money request not found"));

        if (!request.getRequestee().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to accept this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Money request is no longer pending");
        }

        // Fulfill the transaction!
        // The sender of the transaction is the requestee, paying money to the
        // requester.
        transactionService.sendMoney(userId, request.getRequester().getId(), request.getAmount(),
                "Request Accepted: " + request.getPurpose(), pin);

        request.setStatus(RequestStatus.ACCEPTED);
        MoneyRequest savedRequest = moneyRequestRepository.save(request);

        // If this request was linked to an invoice, mark the invoice as PAID
        if (savedRequest.getInvoice() != null) {
            com.rev.app.entity.Invoice invoice = savedRequest.getInvoice();
            invoice.setStatus(com.rev.app.entity.Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        markNotificationAsRead(requestId);

        return moneyRequestMapper.toDTO(savedRequest);
    }

    private void markNotificationAsRead(Long requestId) {
        notificationRepository.findAll().stream()
                .filter(n -> com.rev.app.entity.Notification.NotificationType.MONEY_REQUEST.equals(n.getType()))
                .filter(n -> requestId.equals(n.getTargetId()))
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    @Override
    @Transactional
    public MoneyRequestDTO declineRequest(Long requestId, Long userId) {
        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Money request not found"));

        if (!request.getRequestee().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to decline this request");
        }

        request.setStatus(RequestStatus.DECLINED);
        MoneyRequest declined = moneyRequestRepository.save(request);

        // Notify the requester that their request was declined
        String requesteeName = request.getRequestee().getFullName();
        notificationService.createNotification(
                request.getRequester().getId(),
                String.format("%s has declined your request of INR %.2f for %s",
                        requesteeName, request.getAmount(), request.getPurpose()),
                com.rev.app.entity.Notification.NotificationType.MONEY_REQUEST,
                declined.getId());

        markNotificationAsRead(requestId);
        return moneyRequestMapper.toDTO(declined);
    }

    @Override
    @Transactional
    public MoneyRequestDTO cancelRequest(Long requestId, Long userId) {
        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Money request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this request");
        }

        request.setStatus(RequestStatus.CANCELLED);
        return moneyRequestMapper.toDTO(moneyRequestRepository.save(request));
    }
}
