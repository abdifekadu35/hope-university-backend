package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Enrollment;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    Page<Enrollment> findByStudent(Student student, Pageable pageable);

    Page<Enrollment> findByCourse(Course course, Pageable pageable);

    Page<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status, Pageable pageable);

    // Method used by StatisticsService
    Page<Enrollment> findByStatus(EnrollmentStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Enrollment e SET e.status = :status WHERE e.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") EnrollmentStatus status);

    boolean existsByStudentAndCourseAndStatusNot(Student student, Course course, EnrollmentStatus excludedStatus);
    long countByStatus(EnrollmentStatus status);

}
