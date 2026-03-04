package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.Invoice;
import com.rev.app.entity.Invoice.InvoiceStatus;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class IInvoiceRepositoryTest {

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByBusinessUserId_ReturnsInvoices() {
        User businessUser = new User();
        businessUser.setEmail("biz2@test.com");
        businessUser.setFullName("Biz User 2");
        businessUser.setPassword("pass");
        businessUser.setRole(Role.BUSINESS);
        businessUser = entityManager.persistAndFlush(businessUser);

        Invoice inv = new Invoice();
        inv.setBusinessUser(businessUser);
        inv.setCustomerName("Customer");
        inv.setCustomerEmail("cust@t.com");
        inv.setStatus(InvoiceStatus.DRAFT);
        inv.setDueDate(java.time.LocalDate.now().plusDays(30));
        entityManager.persistAndFlush(inv);

        List<Invoice> found = invoiceRepository.findByBusinessUserId(businessUser.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
