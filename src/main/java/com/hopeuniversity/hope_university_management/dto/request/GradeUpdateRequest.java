package com.hopeuniversity.hope_university_management.dto.request;

import lombok.Data;

@Data
public class GradeUpdateRequest {
    private Double grade; // 0-100
    private String letterGrade; // optional, can be computed from grade
}