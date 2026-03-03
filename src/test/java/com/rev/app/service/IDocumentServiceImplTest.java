package com.rev.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.rev.app.dto.DocumentDTO;
import com.rev.app.entity.Document;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.IDocumentRepository;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class IDocumentServiceImplTest {

    @Mock
    private IDocumentRepository documentRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private IDocumentServiceImpl documentService;

    private User user;
    private Document document;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        document = new Document();
        document.setId(50L);
        document.setUser(user);
        document.setFileName("test.pdf");
        document.setDocumentType("ID_PROOF");
        document.setVerified(false);
    }

    @Test
    void uploadDocument_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentDTO result = documentService.uploadDocument(1L, "ID_PROOF", file);

        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void getDocumentById_Success() {
        when(documentRepository.findById(50L)).thenReturn(Optional.of(document));

        DocumentDTO result = documentService.getDocumentById(50L);

        assertNotNull(result);
        assertEquals(50L, result.getId());
    }

    @Test
    void verifyDocument_Success() {
        when(documentRepository.findById(50L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        assertDoesNotThrow(() -> documentService.verifyDocument(50L));
        assertTrue(document.isVerified());
    }

    @Test
    void deleteDocument_Success() {
        when(documentRepository.existsById(50L)).thenReturn(true);
        doNothing().when(documentRepository).deleteById(50L);

        assertDoesNotThrow(() -> documentService.deleteDocument(50L));
        verify(documentRepository).deleteById(50L);
    }
}
