package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private Long userId;
    private String fileName;
    private String documentType;
    private boolean isVerified;
    private String uploadedAt;
}
