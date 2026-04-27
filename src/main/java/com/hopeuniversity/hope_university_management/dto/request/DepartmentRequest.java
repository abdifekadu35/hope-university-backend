package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    private Long headInstructorId;
}