package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Document;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByStudent(Student student, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<Document> findActiveById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Document d SET d.deletedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    void softDeleteById(@Param("id") Long id);
}