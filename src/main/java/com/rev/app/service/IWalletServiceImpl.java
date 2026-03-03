package com.rev.app.service;

import com.rev.app.dto.WalletDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.Wallet;
import com.rev.app.entity.Transaction;
import com.rev.app.entity.Transaction.TransactionType;
import com.rev.app.entity.Transaction.TransactionStatus;
import com.rev.app.exception.InsufficientFundsException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.WalletMapper;
import com.rev.app.repository.ITransactionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.repository.IWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IWalletServiceImpl implements IWalletService {

    private final IWalletRepository walletRepository;
    private final IUserRepository userRepository;
    private final WalletMapper walletMapper;
    private final ITransactionRepository transactionRepository;
    private final IEmailService emailService;

    @Autowired
    public IWalletServiceImpl(IWalletRepository walletRepository, IUserRepository userRepository, WalletMapper walletMapper,
                              ITransactionRepository transactionRepository, IEmailService emailService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public WalletDTO createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Wallet already exists for user: " + userId);
        }

        Wallet wallet = new Wallet(user);
        wallet.setBalance(BigDecimal.ZERO);
        
        return walletMapper.toDTO(walletRepository.save(wallet));
    }

    @Override
    @Cacheable(value = "wallets", key = "#userId")
    public WalletDTO getWalletByUserId(Long userId) {
        log.info("Fetching wallet balance from DB for user ID: {}", userId);
        return walletRepository.findByUserId(userId)
                .map(walletMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    @Override
    @Transactional(noRollbackFor = {IllegalArgumentException.class})
    @Caching(evict = {
        @CacheEvict(value = "wallets", key = "#userId"),
        @CacheEvict(value = "analytics", key = "#userId")
    })
    public WalletDTO addFunds(Long userId, BigDecimal amount, Long paymentMethodId) {
        log.info("Adding {} funds to wallet for user ID: {}", amount, userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Failed to add funds: Amount must be greater than zero for user ID: {}", userId);
            
            // Record Transaction
            Transaction tx = new Transaction();
            tx.setSender(wallet.getUser());
            tx.setRecipient(wallet.getUser());
            tx.setAmount(amount);
            tx.setType(TransactionType.ADD_FUNDS);
            tx.setStatus(TransactionStatus.FAILED);
            tx.setNote("Failed: Amount must be greater than zero");
            transactionRepository.saveAndFlush(tx);
            
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Logic to simulate pulling from paymentMethodId goes here. 
        // We assume success for the simulation.
        
        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setSender(wallet.getUser());
        tx.setRecipient(wallet.getUser());
        tx.setAmount(amount);
        tx.setType(TransactionType.ADD_FUNDS);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setNote("Added funds via payment method");
        transactionRepository.save(tx);
        
        emailService.sendTransactionNotification(wallet.getUser().getEmail(), 
                "You have successfully added " + amount + " to your wallet.");

        return walletMapper.toDTO(savedWallet);
    }

    @Override
    @Transactional(noRollbackFor = {InsufficientFundsException.class, IllegalArgumentException.class})
    @Caching(evict = {
        @CacheEvict(value = "wallets", key = "#userId"),
        @CacheEvict(value = "analytics", key = "#userId")
    })
    public WalletDTO withdrawFunds(Long userId, BigDecimal amount, Long paymentMethodId) {
        log.info("Withdrawing {} funds from wallet for user ID: {}", amount, userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Failed to withdraw funds: Amount must be greater than zero for user ID: {}", userId);
            
            Transaction tx = new Transaction();
            tx.setSender(wallet.getUser());
            tx.setRecipient(wallet.getUser());
            tx.setAmount(amount);
            tx.setType(TransactionType.WITHDRAW);
            tx.setStatus(TransactionStatus.FAILED);
            tx.setNote("Failed: Amount must be greater than zero");
            transactionRepository.saveAndFlush(tx);

            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Failed to withdraw funds: Insufficient funds {} for withdrawal of {}", wallet.getBalance(), amount);
            
            Transaction tx = new Transaction();
            tx.setSender(wallet.getUser());
            tx.setRecipient(wallet.getUser());
            tx.setAmount(amount);
            tx.setType(TransactionType.WITHDRAW);
            tx.setStatus(TransactionStatus.FAILED);
            tx.setNote("Failed: Insufficient funds");
            transactionRepository.saveAndFlush(tx);

            throw new InsufficientFundsException("Insufficient funds to withdraw: " + amount);
        }

        // Logic to simulate pushing to bank account goes here.
        
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setSender(wallet.getUser());
        tx.setRecipient(wallet.getUser());
        tx.setAmount(amount);
        tx.setType(TransactionType.WITHDRAW);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setNote("Withdrawn funds to payment method");
        transactionRepository.save(tx);
        
        emailService.sendTransactionNotification(wallet.getUser().getEmail(), 
                "You have successfully withdrawn " + amount + " from your wallet.");

        return walletMapper.toDTO(savedWallet);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "wallets", key = "#userId"),
        @CacheEvict(value = "analytics", key = "#userId")
    })
    public WalletDTO adminAddFunds(Long userId, BigDecimal amount) {
        log.info("Admin adding {} funds to wallet for user ID: {}", amount, userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setSender(wallet.getUser()); // System/Admin action
        tx.setRecipient(wallet.getUser());
        tx.setAmount(amount);
        tx.setType(TransactionType.ADD_FUNDS);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setNote("Funds added by Administrator");
        transactionRepository.save(tx);
        
        emailService.sendTransactionNotification(wallet.getUser().getEmail(), 
                "An administrator has added " + amount + " to your wallet.");

        return walletMapper.toDTO(savedWallet);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "wallets", key = "#userId"),
        @CacheEvict(value = "analytics", key = "#userId")
    })
    public WalletDTO adminDeductFunds(Long userId, BigDecimal amount) {
        log.info("Admin deducting {} funds from wallet for user ID: {}", amount, userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("User has insufficient funds for admin deduction: " + amount);
        }
        
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setSender(wallet.getUser());
        tx.setRecipient(wallet.getUser()); // System/Admin action
        tx.setAmount(amount);
        tx.setType(TransactionType.WITHDRAW);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setNote("Funds deducted by Administrator");
        transactionRepository.save(tx);
        
        emailService.sendTransactionNotification(wallet.getUser().getEmail(), 
                "An administrator has deducted " + amount + " from your wallet.");

        return walletMapper.toDTO(savedWallet);
    }

    @Override
    public boolean hasSufficientBalance(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        return wallet.getBalance().compareTo(amount) >= 0;
    }
}
