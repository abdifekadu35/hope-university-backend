package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.EnrollmentRequest;
import com.hopeuniversity.hope_university_management.dto.request.GradeUpdateRequest;
import com.hopeuniversity.hope_university_management.dto.response.EnrollmentResponse;
import com.hopeuniversity.hope_university_management.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Page<EnrollmentResponse>> getAllEnrollments(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @enrollmentSecurity.canViewEnrollment(#id, authentication)")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByStudent(@PathVariable Long studentId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId, pageable));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByCourse(@PathVariable Long courseId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        return new ResponseEntity<>(enrollmentService.enrollStudent(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Void> dropEnrollment(@PathVariable Long id) {
        enrollmentService.dropEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/grade")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<EnrollmentResponse> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeUpdateRequest request) {
        return ResponseEntity.ok(enrollmentService.updateGrade(id, request));
    }
}