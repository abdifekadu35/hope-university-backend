package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorResponse {
    private Long id;
    private String instructorId;
    private Long userId;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private String office;
    private String phone;
    private LocalDateTime hireDate;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}