package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByUser(User user);

    Optional<Instructor> findByInstructorId(String instructorId);

    @Query("SELECT i FROM Instructor i WHERE i.deletedAt IS NULL")
    Page<Instructor> findAllActive(Pageable pageable);

    @Query("SELECT i FROM Instructor i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Instructor> findActiveById(@Param("id") Long id);

    @Query("SELECT i FROM Instructor i WHERE i.deletedAt IS NULL")
    List<Instructor> findAllActive();

    @Query("SELECT COUNT(i) FROM Instructor i WHERE i.deletedAt IS NULL")
    long countActive();

    boolean existsByInstructorId(String instructorId);
}