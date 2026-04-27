package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE e.deletedAt IS NULL")
    Page<Exam> findAllActive(Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Exam> findActiveById(@Param("id") Long id);

    Page<Exam> findByCourse(Course course, Pageable pageable);
}