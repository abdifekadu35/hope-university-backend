package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Department;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    @Query("SELECT c FROM Course c WHERE c.deletedAt IS NULL")
    Page<Course> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Course> findActiveById(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.deletedAt IS NULL")
    long countActive();

    Page<Course> findByDepartment(Department department, Pageable pageable);
    Page<Course> findByInstructor(Instructor instructor, Pageable pageable);

    boolean existsByCode(String code);
    boolean existsByName(String name);
}