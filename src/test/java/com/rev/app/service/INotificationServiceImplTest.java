package com.rev.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rev.app.dto.NotificationDTO;
import com.rev.app.entity.Notification;
import com.rev.app.entity.Notification.NotificationType;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.NotificationMapper;
import com.rev.app.repository.INotificationRepository;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class INotificationServiceImplTest {

    @Mock
    private INotificationRepository notificationRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private INotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private NotificationDTO notificationDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setTransactionAlerts(true);
        user.setSecurityAlerts(true);

        notification = new Notification();
        notification.setId(40L);
        notification.setUser(user);
        notification.setMessage("Test notification");
        notification.setRead(false);

        notificationDTO = new NotificationDTO();
        notificationDTO.setId(40L);
    }

    @Test
    void createNotification_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toDTO(any(Notification.class))).thenReturn(notificationDTO);

        NotificationDTO result = notificationService.createNotification(1L, "Test message", NotificationType.TRANSACTION);

        assertNotNull(result);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_PreferencesDisabled_ReturnsNull() {
        user.setTransactionAlerts(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NotificationDTO result = notificationService.createNotification(1L, "Test message", NotificationType.TRANSACTION);

        assertNull(result);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void getUserNotifications_Success() {
        when(notificationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(notification));
        when(notificationMapper.toDTO(notification)).thenReturn(notificationDTO);

        List<NotificationDTO> result = notificationService.getUserNotifications(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(40L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        assertDoesNotThrow(() -> notificationService.markAsRead(40L));
        assertTrue(notification.isRead());
    }
}
