package com.hopeuniversity.hope_university_management.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated student ID (set by service, not by user)
    @Column(unique = true, nullable = false)
    private String studentId;

    // Name parts
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String fatherName;  // Middle name / Father's name

    @Column(nullable = false)
    private String lastName;

    @Transient
    public String getFullName() {
        return firstName + " " + fatherName + " " + lastName;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    private Integer enrollmentYear;
    private String phone;
    private String address;          // kept for compatibility
    private LocalDateTime dateOfBirth;
    private String profilePictureUrl;

    // Student Identity
    @Column(length = 50)
    private String nationalId;
    private String placeOfBirth;
    private String gender;           // MALE, FEMALE, OTHER

    // Detailed current address
    private String currentAddress;
    private String country;
    private String city;
    private String postalCode;

    // Academic Profile
    private String faculty;
    private String program;
    private String modeOfStudy;      // Regular, Extension, Distance
    private String academicStatus;   // Active, On Leave, Graduated, Withdrawn

    // Guardian / Emergency Contacts
    private String guardianFullName;
    private String guardianRelationship;
    private String guardianPhone;
    private String guardianEmail;

    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}