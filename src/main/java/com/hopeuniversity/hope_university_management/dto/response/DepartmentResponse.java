package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long headInstructorId;
    private String headInstructorName;
    private Integer instructorCount;
    private Integer studentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}