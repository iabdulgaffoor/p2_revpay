package com.revpay.application.service;

import com.revpay.application.dto.SendMoneyRequestDto;
import com.revpay.common.exception.BusinessException;
import com.revpay.common.exception.InsufficientBalanceException;
import com.revpay.common.exception.ResourceNotFoundException;
import com.revpay.domain.model.*;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.model.enums.TransactionType;
import com.revpay.domain.model.enums.UserStatus;
import com.revpay.domain.repository.ITransactionRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.domain.repository.IWalletRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements ITransactionService {

        private final IUserRepo userRepo;
        private final IWalletRepo walletRepo;
        private final ITransactionRepo transactionRepo;
        private final PasswordEncoder passwordEncoder;
        private final INotificationService notificationService;

        @Override
        @Transactional
        public void sendMoney(Long senderId, SendMoneyRequestDto request) {
                log.info("Initiating money transfer from sender ID: {} for amount: {}",
                                senderId, request.getAmount());

                User sender = userRepo.findById(senderId)
                                .orElseThrow(() -> {
                                        log.error("Transfer failed: Sender with ID {} not found", senderId);
                                        return new ResourceNotFoundException("Sender not found");
                                });

                if (sender.getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Sender account is " + sender.getStatus(), "ACCOUNT_SUSPENDED");
                }

                User receiver;
                if (request.getReceiverId() != null) {
                        receiver = userRepo.findById(request.getReceiverId())
                                        .orElseThrow(() -> {
                                                log.error("Transfer failed: Receiver with ID {} not found",
                                                                request.getReceiverId());
                                                return new ResourceNotFoundException("Receiver not found");
                                        });
                } else if (request.getReceiverIdentifier() != null) {
                        receiver = userRepo.findByIdentifier(request.getReceiverIdentifier())
                                        .orElseThrow(() -> {
                                                log.error("Transfer failed: Receiver identifier '{}' not found",
                                                                request.getReceiverIdentifier());
                                                return new ResourceNotFoundException(
                                                                "Receiver not found: "
                                                                                + request.getReceiverIdentifier());
                                        });
                } else {
                        throw new BusinessException("Receiver ID or identifier must be provided", "INVALID_REQUEST");
                }

                if (receiver.getStatus() != UserStatus.ACTIVE) {
                        throw new BusinessException("Receiver account is " + receiver.getStatus(), "ACCOUNT_SUSPENDED");
                }

                TransactionPin transactionPin = sender.getTransactionPin();
                if (transactionPin == null || !passwordEncoder.matches(request.getPin(), transactionPin.getPinHash())) {
                        throw new BusinessException("Invalid Transaction PIN", "INVALID_PIN");
                }

                Wallet senderWallet = walletRepo.findByUserId(senderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

                Wallet receiverWallet = walletRepo.findByUserId(receiver.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

                if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
                        throw new InsufficientBalanceException();
                }

                senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
                receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

                walletRepo.save(senderWallet);
                walletRepo.save(receiverWallet);

                Transaction transaction = Transaction.builder()
                                .transactionId(UUID.randomUUID().toString())
                                .sender(sender)
                                .receiver(receiver)
                                .amount(request.getAmount())
                                .type(TransactionType.TRANSFER)
                                .status(TransactionStatus.COMPLETED)
                                .description(request.getDescription())
                                .build();

                transactionRepo.save(transaction);

                notificationService.sendNotification(sender, "Money Sent",
                                "You sent " + request.getAmount() + " to " + receiver.getFullName(), "TRANSACTION");
                notificationService.sendNotification(receiver, "Money Received",
                                "You received " + request.getAmount() + " from " + sender.getFullName(), "TRANSACTION");
        }
}
