package com.rev.app.service;

import com.rev.app.dto.DocumentDTO;
import com.rev.app.entity.Document;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.IDocumentRepository;
import com.rev.app.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IDocumentServiceImpl implements IDocumentService {

    private final IDocumentRepository documentRepository;
    private final IUserRepository userRepository;

    @Autowired
    public IDocumentServiceImpl(IDocumentRepository documentRepository, IUserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DocumentDTO uploadDocument(Long userId, String documentType, MultipartFile file) {
        log.info("Uploading document type {} for user ID: {}", documentType, userId);
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        try {
            Document doc = new Document();
            doc.setUser(user);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(file.getContentType());
            doc.setDocumentType(documentType);
            doc.setData(file.getBytes());
            doc.setVerified(false);

            Document savedDoc = documentRepository.save(doc);
            return convertToDTO(savedDoc);
        } catch (IOException e) {
            log.error("Failed to store file data", e);
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public DocumentDTO getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
    }

    @Override
    public List<DocumentDTO> getDocumentsByUserId(Long userId) {
        return documentRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void verifyDocument(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
        doc.setVerified(true);
        documentRepository.save(doc);
        
        // Optional: If this is the final document required, mark User Business Status as Verified.
        log.info("Document ID {} has been marked as verified.", documentId);
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document not found: " + documentId);
        }
        documentRepository.deleteById(documentId);
        log.info("Document ID {} deleted.", documentId);
    }

    private DocumentDTO convertToDTO(Document doc) {
        return new DocumentDTO(
                doc.getId(),
                doc.getUser().getId(),
                doc.getFileName(),
                doc.getDocumentType(),
                doc.isVerified(),
                doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null
        );
    }
}
