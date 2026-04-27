package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private Long departmentId;
    private String departmentName;
    private Long instructorId;
    private String instructorName;
    private Integer enrolledStudentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}