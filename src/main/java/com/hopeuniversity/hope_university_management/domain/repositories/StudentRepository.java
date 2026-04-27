package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);
    Optional<Student> findTopByOrderByIdDesc();
    Optional<Student> findByStudentId(String studentId);

    @Query("SELECT s FROM Student s WHERE s.deletedAt IS NULL")
    Page<Student> findAllActive(Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Student> findActiveById(@Param("id") Long id);

    @Query("SELECT s FROM Student s WHERE s.deletedAt IS NULL")
    List<Student> findAllActive();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.deletedAt IS NULL")
    long countActive();

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    void softDeleteById(@Param("id") Long id);

    boolean existsByStudentId(String studentId);
}