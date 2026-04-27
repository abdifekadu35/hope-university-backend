package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Exam;
import com.hopeuniversity.hope_university_management.domain.entities.Grade;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByStudentAndExam(Student student, Exam exam);

    Page<Grade> findByStudent(Student student, Pageable pageable);

    Page<Grade> findByExam(Exam exam, Pageable pageable);
}