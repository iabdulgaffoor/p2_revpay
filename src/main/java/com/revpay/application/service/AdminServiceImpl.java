package com.revpay.application.service;

import com.revpay.common.exception.ResourceNotFoundException;
import com.revpay.domain.model.BusinessProfile;
import com.revpay.domain.model.Loan;
import com.revpay.domain.model.Transaction;
import com.revpay.domain.model.User;
import com.revpay.domain.model.Wallet;
import com.revpay.domain.model.enums.UserStatus;
import com.revpay.domain.model.enums.AccountType;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.repository.IBusinessProfileRepo;
import com.revpay.domain.repository.ILoanRepo;
import com.revpay.domain.repository.ITransactionRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.domain.repository.IWalletRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements IAdminService {

    private final IUserRepo userRepo;
    private final IBusinessProfileRepo businessProfileRepo;
    private final ILoanRepo loanRepo;
    private final ITransactionRepo transactionRepo;
    private final IWalletRepo walletRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void approveBusinessProfile(Long profileId) {
        BusinessProfile profile = businessProfileRepo.findByUserId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Business Profile not found"));
        profile.setVerified(true);
        businessProfileRepo.save(profile);

        User user = profile.getUser();
        user.setAccountType(AccountType.BUSINESS);
        userRepo.save(user);
        log.info("Business profile approved for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void rejectBusinessProfile(Long profileId) {
        // Logic for rejection
        log.info("Business profile rejected for profile ID: {}", profileId);
    }

    @Override
    @Transactional
    public void approveLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        loan.setStatus(TransactionStatus.COMPLETED);
        loanRepo.save(loan);

        Wallet wallet = walletRepo.findByUserId(loan.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(loan.getPrincipalAmount()));
        walletRepo.save(wallet);

        log.info("Loan approved and credited for user: {}", loan.getUser().getEmail());
    }

    @Override
    @Transactional
    public void rejectLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loan.setStatus(TransactionStatus.FAILED);
        loanRepo.save(loan);
    }

    @Override
    @Transactional
    public void cancelTransaction(String transactionId) {
        Transaction tx = transactionRepo.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (tx.getStatus() == TransactionStatus.COMPLETED) {
            Wallet senderWallet = walletRepo.findByUserId(tx.getSender().getId()).get();
            Wallet receiverWallet = walletRepo.findByUserId(tx.getReceiver().getId()).get();

            receiverWallet.setBalance(receiverWallet.getBalance().subtract(tx.getAmount()));
            senderWallet.setBalance(senderWallet.getBalance().add(tx.getAmount()));

            walletRepo.save(senderWallet);
            walletRepo.save(receiverWallet);

            tx.setStatus(TransactionStatus.CANCELLED);
            transactionRepo.save(tx);
            log.info("Transaction {} cancelled by admin", transactionId);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setDeleted(true);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getCredential().setPasswordHash(passwordEncoder.encode(newPassword));
        user.getCredential().setPasswordLastChanged(LocalDateTime.now());
        userRepo.save(user);
        log.info("Password reset for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetTransactionPin(Long userId, String newPin) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getTransactionPin().setPinHash(passwordEncoder.encode(newPin));
        userRepo.save(user);
        log.info("Transaction PIN reset for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.SUSPENDED);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepo.save(user);
        log.info("Status toggled for user: {} to {}", user.getEmail(), user.getStatus());
    }

    @Override
    @Transactional
    public void addMoneyToUser(Long userId, java.math.BigDecimal amount) {
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepo.save(wallet);
        log.info("Admin credited ${} to user ID: {}", amount, userId);
    }
}
