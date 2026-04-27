package com.hopeuniversity.hope_university_management.dto.response;

import com.hopeuniversity.hope_university_management.domain.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private EnrollmentStatus status;
    private LocalDateTime enrollmentDate;
    private Double grade;
    private String letterGrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}