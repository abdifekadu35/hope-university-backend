package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentRequest {

    // Name parts (required)
    @NotBlank
    private String firstName;
    @NotBlank
    private String fatherName;
    @NotBlank
    private String lastName;

    // User account info
    @Email
    @NotBlank
    private String email;   // used for login

    // Student profile fields
    private Long departmentId;
    private Integer enrollmentYear;
    private String phone;
    private String address;       // legacy
    private LocalDateTime dateOfBirth;
    private String profilePictureUrl;

    // Student Identity
    private String nationalId;
    private String placeOfBirth;
    private String gender;

    // Detailed address
    private String currentAddress;
    private String country;
    private String city;
    private String postalCode;

    // Academic Profile
    private String faculty;
    private String program;
    private String modeOfStudy;
    private String academicStatus;

    // Guardian
    private String guardianFullName;
    private String guardianRelationship;
    private String guardianPhone;
    private String guardianEmail;

    // Note: studentId and password are NOT in request – they are generated
}