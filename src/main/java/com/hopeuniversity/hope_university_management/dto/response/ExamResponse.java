package com.hopeuniversity.hope_university_management.dto.response;

import com.hopeuniversity.hope_university_management.domain.enums.ExamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String title;
    private ExamType examType;
    private Double maxMarks;
    private LocalDate examDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}