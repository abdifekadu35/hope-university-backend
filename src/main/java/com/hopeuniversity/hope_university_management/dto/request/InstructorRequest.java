package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InstructorRequest {

    private Long userId;

    private String fullName;
    private String email;
    private String password;

    // NOT @NotBlank anymore – optional, backend will generate if missing
    private String instructorId;

    private Long departmentId;
    private String office;
    private String phone;
    private LocalDateTime hireDate;
    private String title;
}