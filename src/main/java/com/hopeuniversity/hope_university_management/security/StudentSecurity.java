package com.hopeuniversity.hope_university_management.security;

import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("studentSecurity")
@RequiredArgsConstructor
public class StudentSecurity {

    private final StudentRepository studentRepository;

    public boolean isStudentOwner(Long studentId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();
        return studentRepository.findById(studentId)
                .map(student -> student.getUser().getId().equals(userId))
                .orElse(false);
    }
}