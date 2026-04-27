package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSimpleResponse {
    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private String departmentName;
}