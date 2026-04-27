package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Attendance;
import com.hopeuniversity.hope_university_management.domain.entities.ClassSession;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentAndClassSessionAndDate(Student student, ClassSession classSession, LocalDate date);

    Page<Attendance> findByStudent(Student student, Pageable pageable);

    Page<Attendance> findByClassSession(ClassSession classSession, Pageable pageable);

    Page<Attendance> findByStudentAndStatus(Student student, AttendanceStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Attendance a SET a.status = :status, a.remarks = :remarks WHERE a.id = :id")
    void updateAttendanceStatus(@Param("id") Long id, @Param("status") AttendanceStatus status, @Param("remarks") String remarks);

    // Method used by StatisticsService
    long countByStatusAndDateAfter(AttendanceStatus status, LocalDate date);
}