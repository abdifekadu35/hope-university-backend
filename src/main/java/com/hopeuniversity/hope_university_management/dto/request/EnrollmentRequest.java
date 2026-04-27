package com.hopeuniversity.hope_university_management.dto.request;

import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long studentId;
    private Long courseId;
}