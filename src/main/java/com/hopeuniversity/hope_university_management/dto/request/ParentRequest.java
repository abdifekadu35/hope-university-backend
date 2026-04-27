package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class ParentRequest {

    // Option A: link to existing user (user must have PARENT role)
    private Long userId;

    // Option B: create new user
    private String fullName;
    @Email
    private String email;
    private String password;

    private String phone;
    private String address;
    private String occupation;

    // List of student IDs to link as children
    private List<Long> childStudentIds;
}