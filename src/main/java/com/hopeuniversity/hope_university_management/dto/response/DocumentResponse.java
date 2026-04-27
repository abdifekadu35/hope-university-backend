package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String description;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}