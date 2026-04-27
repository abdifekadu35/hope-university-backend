package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role; // ADMIN, REGISTRAR, INSTRUCTOR, STUDENT

    private boolean active = true;
}