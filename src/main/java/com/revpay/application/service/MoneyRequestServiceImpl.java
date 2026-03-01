package com.revpay.application.service;

import com.revpay.domain.model.enums.UserStatus;
import com.revpay.application.dto.MoneyRequestDto;
import com.revpay.application.dto.SendMoneyRequestDto;
import com.revpay.common.exception.BusinessException;
import com.revpay.common.exception.ResourceNotFoundException;
import com.revpay.domain.model.MoneyRequest;
import com.revpay.domain.model.User;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.repository.IMoneyRequestRepo;
import com.revpay.domain.repository.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoneyRequestServiceImpl implements IMoneyRequestService {

        private final IUserRepo userRepo;
        private final IMoneyRequestRepo moneyRequestRepo;
        private final ITransactionService transactionService;
        private final INotificationService notificationService;

        @Override
        @Transactional
        public void sendRequest(Long requesterId, MoneyRequestDto request) {
                User requester = userRepo.findById(requesterId)
                                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

                String targetEmail = request.getRequestedFromEmail() != null ? request.getRequestedFromEmail().trim()
                                : "";
                User requestedFrom = userRepo.findByEmailIgnoreCase(targetEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Target user not found: " + targetEmail));

                if (requester.getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Requester account is " + requester.getStatus(),
                                        "ACCOUNT_SUSPENDED");
                }

                if (requestedFrom.getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Target account is " + requestedFrom.getStatus(),
                                        "ACCOUNT_SUSPENDED");
                }

                MoneyRequest moneyRequest = MoneyRequest.builder()
                                .requester(requester)
                                .requestedFrom(requestedFrom)
                                .amount(request.getAmount())
                                .purpose(request.getPurpose())
                                .status(TransactionStatus.PENDING)
                                .build();

                moneyRequestRepo.save(moneyRequest);

                notificationService.sendNotification(requestedFrom, "Payment Request",
                                requester.getFullName() + " is requesting " + request.getAmount() + " for: "
                                                + request.getPurpose(),
                                "REQUEST");
        }

        @Override
        @Transactional
        public void acceptRequest(Long requesteeId, Long requestId, String pin) {
                MoneyRequest request = moneyRequestRepo.findById(requestId)
                                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

                if (!request.getRequestedFrom().getId().equals(requesteeId)) {
                        throw new BusinessException("Not authorized to accept this request", "UNAUTHORIZED");
                }

                if (request.getRequestedFrom().getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Account is " + request.getRequestedFrom().getStatus(),
                                        "ACCOUNT_SUSPENDED");
                }

                if (request.getRequester().getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Requester account is " + request.getRequester().getStatus(),
                                        "ACCOUNT_SUSPENDED");
                }

                if (request.getStatus() != TransactionStatus.PENDING) {
                        throw new BusinessException("Request is not pending", "INVALID_STATUS");
                }

                // Use transaction service to perform the actual money transfer
                SendMoneyRequestDto sendDto = new SendMoneyRequestDto();
                sendDto.setReceiverId(request.getRequester().getId());
                sendDto.setAmount(request.getAmount());
                sendDto.setPin(pin);
                sendDto.setDescription("Replying to request: " + request.getPurpose());

                transactionService.sendMoney(requesteeId, sendDto);

                request.setStatus(TransactionStatus.COMPLETED);
                moneyRequestRepo.save(request);

                notificationService.sendNotification(request.getRequester(), "Request Accepted",
                                request.getRequestedFrom().getFullName() + " accepted your request of "
                                                + request.getAmount(),
                                "REQUEST");
        }

        @Override
        @Transactional
        public void rejectRequest(Long requesteeId, Long requestId) {
                MoneyRequest request = moneyRequestRepo.findById(requestId)
                                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

                if (!request.getRequestedFrom().getId().equals(requesteeId)) {
                        throw new BusinessException("Not authorized to reject this request", "UNAUTHORIZED");
                }

                request.setStatus(TransactionStatus.FAILED);
                moneyRequestRepo.save(request);

                notificationService.sendNotification(request.getRequester(), "Request Rejected",
                                request.getRequestedFrom().getFullName() + " rejected your request of "
                                                + request.getAmount(),
                                "REQUEST");
        }
}
