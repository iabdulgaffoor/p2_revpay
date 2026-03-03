package com.rev.app.rest;

import com.rev.app.dto.DocumentDTO;
import com.rev.app.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentRestController {

    private final IDocumentService documentService;

    @Autowired
    public DocumentRestController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload/user/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadDocument(
            @PathVariable Long userId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        
        DocumentDTO uploadedDoc = documentService.uploadDocument(userId, documentType, file);
        return new ResponseEntity<>(uploadedDoc, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getDocumentsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<Void> verifyDocument(@PathVariable Long id) {
        // Typically secured for ADMIN role only
        documentService.verifyDocument(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
