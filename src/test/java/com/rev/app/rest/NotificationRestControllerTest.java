package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.NotificationDTO;
import com.rev.app.entity.Notification.NotificationType;
import com.rev.app.service.INotificationService;

class NotificationRestControllerTest {

    private INotificationService notificationService;
    private NotificationRestController notificationRestController;

    @BeforeEach
    void setUp() {
        notificationService = mock(INotificationService.class);
        notificationRestController = new NotificationRestController(notificationService);
    }

    @Test
    void createNotification_Success() {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(400L);

        when(notificationService.createNotification(anyLong(), anyString(), any())).thenReturn(dto);

        ResponseEntity<NotificationDTO> response = notificationRestController.createNotification(1L, "Test", NotificationType.TRANSACTION);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(400L, response.getBody().getId());
    }

    @Test
    void getUserNotifications_Success() {
        when(notificationService.getUserNotifications(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<NotificationDTO>> response = notificationRestController.getUserNotifications(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }
}
