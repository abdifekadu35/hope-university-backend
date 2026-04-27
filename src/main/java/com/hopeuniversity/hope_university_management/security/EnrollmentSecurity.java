package com.hopeuniversity.hope_university_management.security;

import com.hopeuniversity.hope_university_management.domain.entities.Enrollment;
import com.hopeuniversity.hope_university_management.domain.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("enrollmentSecurity")
@RequiredArgsConstructor
public class EnrollmentSecurity {

    private final EnrollmentRepository enrollmentRepository;

    public boolean canViewEnrollment(Long enrollmentId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment == null) return false;

        // Check if the authenticated user is the student (via student's user ID)
        Long studentUserId = enrollment.getStudent().getUser().getId();
        if (studentUserId.equals(userId)) return true;

        // Check if the authenticated user is a parent of the student
        // This requires a parent-student relationship. We'll leave it optional for now.

        return false;
    }
}