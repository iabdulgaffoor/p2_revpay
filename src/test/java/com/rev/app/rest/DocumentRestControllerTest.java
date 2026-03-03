package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.rev.app.dto.DocumentDTO;
import com.rev.app.service.IDocumentService;

class DocumentRestControllerTest {

    private IDocumentService documentService;
    private DocumentRestController documentRestController;

    @BeforeEach
    void setUp() {
        documentService = mock(IDocumentService.class);
        documentRestController = new DocumentRestController(documentService);
    }

    @Test
    void uploadDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());
        DocumentDTO dto = new DocumentDTO();
        dto.setId(50L);
        dto.setFileName("test.pdf");

        when(documentService.uploadDocument(anyLong(), anyString(), any())).thenReturn(dto);

        ResponseEntity<DocumentDTO> response = documentRestController.uploadDocument(1L, "ID_PROOF", file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test.pdf", response.getBody().getFileName());
    }

    @Test
    void getDocumentById_Success() {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(50L);

        when(documentService.getDocumentById(50L)).thenReturn(dto);

        ResponseEntity<DocumentDTO> response = documentRestController.getDocumentById(50L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50L, response.getBody().getId());
    }
}
