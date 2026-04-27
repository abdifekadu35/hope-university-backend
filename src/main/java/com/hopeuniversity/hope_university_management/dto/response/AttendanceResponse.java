package com.hopeuniversity.hope_university_management.dto.response;

import com.hopeuniversity.hope_university_management.domain.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long classSessionId;
    private String courseCode;
    private String courseName;
    private String instructorName;
    private AttendanceStatus status;
    private LocalDate date;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}