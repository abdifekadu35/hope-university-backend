package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Fee;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("SELECT f FROM Fee f WHERE f.deletedAt IS NULL")
    Page<Fee> findAllActive(Pageable pageable);

    @Query("SELECT f FROM Fee f WHERE f.id = :id AND f.deletedAt IS NULL")
    Optional<Fee> findActiveById(@Param("id") Long id);

    Page<Fee> findByStudent(Student student, Pageable pageable);

    List<Fee> findByStudentAndDueDateBeforeAndDeletedAtIsNull(Student student, LocalDate date);

    // Method used by StatisticsService – sum of pending fees (fees with no fully paid payments)
    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.deletedAt IS NULL AND NOT EXISTS " +
            "(SELECT p FROM Payment p WHERE p.fee = f AND p.status = 'PAID')")
    Double sumPendingFees();
}