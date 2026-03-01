package com.revpay.application.service;

import com.revpay.application.dto.SendMoneyRequestDto;
import com.revpay.common.exception.InsufficientBalanceException;
import com.revpay.domain.model.TransactionPin;
import com.revpay.domain.model.User;
import com.revpay.domain.model.Wallet;
import com.revpay.domain.repository.ITransactionRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.domain.repository.IWalletRepo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TransactionServiceSmokeTest {

    @Mock
    private IUserRepo userRepo;
    @Mock
    private IWalletRepo walletRepo;
    @Mock
    private ITransactionRepo transactionRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = InsufficientBalanceException.class)
    public void testSendMoneyInsufficientBalance() {
        // Arrange
        Long senderId = 1L;
        SendMoneyRequestDto request = SendMoneyRequestDto.builder()
                .receiverId(2L)
                .amount(new BigDecimal("2000.00"))
                .pin("1234")
                .build();

        User sender = User.builder().fullName("Sender")
                .transactionPin(TransactionPin.builder().pinHash("hashed").build()).build();
        User receiver = User.builder().fullName("Receiver").build();
        Wallet senderWallet = Wallet.builder().balance(new BigDecimal("1000.00")).build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepo.findById(2L)).thenReturn(Optional.of(receiver));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(walletRepo.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepo.findByUserId(2L)).thenReturn(Optional.of(Wallet.builder().balance(BigDecimal.ZERO).build()));

        // Act
        transactionService.sendMoney(senderId, request);

        // Assert - Expecting InsufficientBalanceException
    }
}
