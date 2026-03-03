package com.rev.app.service;

import com.rev.app.dto.TransactionDTO;
import com.rev.app.entity.Notification.NotificationType;
import com.rev.app.entity.Transaction;
import com.rev.app.entity.Transaction.TransactionStatus;
import com.rev.app.entity.Transaction.TransactionType;
import com.rev.app.entity.User;
import com.rev.app.entity.Wallet;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.InsufficientFundsException;
import com.rev.app.exception.InvalidCredentialsException;
import com.rev.app.exception.InvalidTransactionPinException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.TransactionMapper;
import com.rev.app.repository.ITransactionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.repository.IWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ITransactionServiceImpl implements ITransactionService {

    private final ITransactionRepository transactionRepository;
    private final IUserRepository userRepository;
    private final IWalletRepository walletRepository;
    private final TransactionMapper transactionMapper;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final INotificationService notificationService;

    @Autowired
    public ITransactionServiceImpl(ITransactionRepository transactionRepository, 
                                   IUserRepository userRepository, 
                                   IWalletRepository walletRepository,
                                   TransactionMapper transactionMapper,
                                   PasswordEncoder passwordEncoder,
                                   IEmailService emailService,
                                   INotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionMapper = transactionMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(noRollbackFor = {InsufficientFundsException.class, BadRequestException.class, InvalidCredentialsException.class, InvalidTransactionPinException.class})
    @Caching(evict = {
        @CacheEvict(value = "wallets", key = "#senderId"),
        @CacheEvict(value = "wallets", key = "#recipientId"),
        @CacheEvict(value = "analytics", key = "#senderId"),
        @CacheEvict(value = "analytics", key = "#recipientId")
    })
    public TransactionDTO sendMoney(Long senderId, Long recipientId, BigDecimal amount, String note, String pin) {
        log.info("Initiating sendMoney from sender ID {} to recipient ID {} for amount {}", senderId, recipientId, amount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Transfer failed: Amount must be greater than zero");
            throw new BadRequestException("Amount must be greater than zero");
        }
        if (senderId.equals(recipientId)) {
            log.error("Transfer failed: Sender and recipient are the same user ID {}", senderId);
            throw new BadRequestException("Cannot send money to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        if (sender.getTransactionPin() == null) {
            log.warn("Transfer failed: No transaction PIN set for sender ID {}", senderId);
            throw new InvalidTransactionPinException("You have not set a transaction PIN. Please set one in your Profile settings before sending money.");
        }
        if (!passwordEncoder.matches(pin, sender.getTransactionPin())) {
            log.warn("Transfer failed: Incorrect transaction PIN for sender ID {}", senderId);
            throw new InvalidTransactionPinException("Incorrect transaction PIN. Please try again.");
        }

        Wallet senderWallet = walletRepository.findByUserId(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));
        Wallet recipientWallet = walletRepository.findByUserId(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient wallet not found"));

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            log.warn("Transfer failed: Insufficient funds. Sender balance: {}, Transfer amount: {}", senderWallet.getBalance(), amount);
            Transaction failedTx = new Transaction();
            failedTx.setSender(sender);
            failedTx.setRecipient(recipient);
            failedTx.setAmount(amount);
            failedTx.setType(TransactionType.SEND);
            failedTx.setStatus(TransactionStatus.FAILED);
            failedTx.setNote("Failed: Insufficient Funds. " + note);
            transactionRepository.saveAndFlush(failedTx); // Force flush to DB before exception

            throw new InsufficientFundsException("Insufficient funds to send: " + amount);
        }

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepository.save(senderWallet);

        // Credit to recipient
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));
        walletRepository.save(recipientWallet);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setRecipient(recipient);
        tx.setAmount(amount);
        tx.setType(TransactionType.SEND);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setNote(note);
        
        Transaction savedTx = transactionRepository.save(tx);
        
        // Save in-app notifications for both sender and recipient
        notificationService.createNotification(sender.getId(),
                "You sent $" + amount + " to " + recipient.getFullName() + (note != null && !note.isEmpty() ? " — " + note : "") + ".",
                NotificationType.TRANSACTION);
        notificationService.createNotification(recipient.getId(),
                "You received $" + amount + " from " + sender.getFullName() + (note != null && !note.isEmpty() ? " — " + note : "") + ".",
                NotificationType.TRANSACTION);

        if (sender.isTransactionAlerts()) {
            emailService.sendTransactionNotification(sender.getEmail(), 
                    "You successfully sent $" + amount + " to " + recipient.getFullName() + ".");
        }
        if (recipient.isTransactionAlerts()) {
            emailService.sendTransactionNotification(recipient.getEmail(), 
                    "You successfully received $" + amount + " from " + sender.getFullName() + ".");
        }

        return transactionMapper.toDTO(savedTx);
    }

    @Override
    public TransactionDTO getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    @Override
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        return transactionRepository.findBySenderIdOrRecipientId(userId, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> filterTransactions(Long userId, TransactionType type, String startDate, String endDate, BigDecimal minAmount, BigDecimal maxAmount, String status) {
        // Advanced filtering - normally uses CriteriaBuilder
        // For simplicity now, returning all for the user 
        // A complete implementation would use Specifications API.
        return getTransactionsByUserId(userId); 
    }

    @Override
    public List<TransactionDTO> searchTransactions(Long userId, String searchTerm) {
        // Custom query to search by term
        return getTransactionsByUserId(userId);
    }

    @Override
    public byte[] exportTransactionsToCSV(Long userId) {
        List<TransactionDTO> txs = getTransactionsByUserId(userId);
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Date,Type,Amount,Status,Sender,Recipient,Note\n");
        for (TransactionDTO t : txs) {
            sb.append(t.getId() + "," + t.getTimestamp() + "," + t.getType() + "," +
                    t.getAmount() + "," + t.getStatus() + "," + t.getSenderName() + "," +
                    t.getRecipientName() + "," + t.getNote() + "\n");
        }
        return sb.toString().getBytes();
    }
}
