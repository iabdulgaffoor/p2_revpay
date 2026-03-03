package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.PaymentMethod;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class IPaymentMethodRepositoryTest {

    @Autowired
    private IPaymentMethodRepository paymentMethodRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_ReturnsMethods() {
        User user = new User();
        user.setEmail("pm@test.com");
        user.setFullName("PM User");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        user = entityManager.persistAndFlush(user);

        PaymentMethod pm = new PaymentMethod();
        pm.setUser(user);
        pm.setType(PaymentMethod.PaymentMethodType.CREDIT_CARD);
        pm.setAccountNumber("****1234");
        pm.setDefault(true);
        entityManager.persistAndFlush(pm);

        List<PaymentMethod> found = paymentMethodRepository.findByUserId(user.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
