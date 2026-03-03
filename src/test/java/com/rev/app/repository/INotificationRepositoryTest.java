package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.Notification;
import com.rev.app.entity.Notification.NotificationType;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class INotificationRepositoryTest {

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_ReturnsNotifications() {
        User user = new User();
        user.setEmail("notif@test.com");
        user.setFullName("Notif User");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        user = entityManager.persistAndFlush(user);

        Notification notif = new Notification();
        notif.setUser(user);
        notif.setMessage("Hello");
        notif.setType(NotificationType.TRANSACTION);
        notif.setRead(false);
        entityManager.persistAndFlush(notif);

        List<Notification> found = notificationRepository.findByUserId(user.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
