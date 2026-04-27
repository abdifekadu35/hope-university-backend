package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String occupation;
    private List<StudentSimpleResponse> children; // nested DTO for basic student info
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}