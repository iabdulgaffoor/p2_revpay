package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;
import com.rev.app.entity.Wallet;

@DataJpaTest
class IWalletRepositoryTest {

    @Autowired
    private IWalletRepository walletRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_ReturnsWallet() {
        User user = new User();
        user.setEmail("wallet@test.com");
        user.setFullName("Wallet User");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        user = entityManager.persistAndFlush(user);

        Wallet wallet = new Wallet(user);
        wallet.setBalance(new BigDecimal("100.00"));
        entityManager.persistAndFlush(wallet);

        Optional<Wallet> found = walletRepository.findByUserId(user.getId());

        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getUser().getId());
    }
}
