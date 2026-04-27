package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {
   // @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private Integer credits;

    private Long departmentId;

    private Long instructorId;
}