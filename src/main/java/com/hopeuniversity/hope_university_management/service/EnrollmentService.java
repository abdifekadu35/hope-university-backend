package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Enrollment;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.enums.EnrollmentStatus;
import com.hopeuniversity.hope_university_management.domain.repositories.CourseRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.EnrollmentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.EnrollmentRequest;
import com.hopeuniversity.hope_university_management.dto.request.GradeUpdateRequest;
import com.hopeuniversity.hope_university_management.dto.response.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public Page<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAll(pageable).map(this::toResponse);
    }

    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return toResponse(enrollment);
    }

    public Page<EnrollmentResponse> getEnrollmentsByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return enrollmentRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    public Page<EnrollmentResponse> getEnrollmentsByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return enrollmentRepository.findByCourse(course, pageable).map(this::toResponse);
    }

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if already enrolled (active enrollment)
        if (enrollmentRepository.existsByStudentAndCourseAndStatusNot(student, course, EnrollmentStatus.DROPPED)) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toResponse(saved);
    }

    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public EnrollmentResponse updateGrade(Long enrollmentId, GradeUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setGrade(request.getGrade());
        if (request.getLetterGrade() != null) {
            enrollment.setLetterGrade(request.getLetterGrade());
        } else if (request.getGrade() != null) {
            enrollment.setLetterGrade(convertGradeToLetter(request.getGrade()));
        }

        // If grade is set and status is ENROLLED, optionally change to COMPLETED
        if (enrollment.getGrade() != null && enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        return toResponse(updated);
    }

    private String convertGradeToLetter(Double grade) {
        if (grade >= 90) return "A";
        if (grade >= 80) return "B";
        if (grade >= 70) return "C";
        if (grade >= 60) return "D";
        return "F";
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getUser().getFullName(),
                enrollment.getStudent().getUser().getEmail(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getCode(),
                enrollment.getCourse().getName(),
                enrollment.getStatus(),
                enrollment.getEnrollmentDate(),
                enrollment.getGrade(),
                enrollment.getLetterGrade(),
                enrollment.getCreatedAt(),
                enrollment.getUpdatedAt()
        );
    }
}