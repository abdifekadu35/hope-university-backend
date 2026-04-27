package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long examId;
    private String examTitle;
    private String courseCode;
    private String courseName;
    private Double marksObtained;
    private Double maxMarks;
    private Double percentage;
    private String letterGrade;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}