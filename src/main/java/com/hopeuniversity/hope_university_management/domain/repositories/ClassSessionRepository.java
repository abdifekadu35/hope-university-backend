package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.ClassSession;
import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    @Query("SELECT cs FROM ClassSession cs WHERE cs.deletedAt IS NULL")
    Page<ClassSession> findAllActive(Pageable pageable);

    @Query("SELECT cs FROM ClassSession cs WHERE cs.id = :id AND cs.deletedAt IS NULL")
    Optional<ClassSession> findActiveById(@Param("id") Long id);

    Page<ClassSession> findByCourse(Course course, Pageable pageable);

    Page<ClassSession> findByInstructor(Instructor instructor, Pageable pageable);
}