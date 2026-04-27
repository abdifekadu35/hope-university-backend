package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentId;
    private Long userId;

    // Name parts
    private String firstName;
    private String fatherName;
    private String lastName;
    private String fullName;   // computed as firstName + fatherName + lastName

    private String email;
    private Long departmentId;
    private String departmentName;
    private Integer enrollmentYear;
    private String phone;
    private String address;          // legacy address
    private LocalDateTime dateOfBirth;
    private String profilePictureUrl;

    // Student Identity
    private String nationalId;
    private String placeOfBirth;
    private String gender;

    // Detailed current address
    private String currentAddress;
    private String country;
    private String city;
    private String postalCode;

    // Academic Profile
    private String faculty;
    private String program;
    private String modeOfStudy;
    private String academicStatus;

    // Guardian / Emergency Contacts
    private String guardianFullName;
    private String guardianRelationship;
    private String guardianPhone;
    private String guardianEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}