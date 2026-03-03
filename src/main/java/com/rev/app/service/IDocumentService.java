package com.rev.app.service;

import com.rev.app.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentService {
    DocumentDTO uploadDocument(Long userId, String documentType, MultipartFile file);
    DocumentDTO getDocumentById(Long documentId);
    List<DocumentDTO> getDocumentsByUserId(Long userId);
    void verifyDocument(Long documentId);
    void deleteDocument(Long documentId);
}
