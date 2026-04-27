package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Parent;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUser(User user);

    @Query("SELECT p FROM Parent p WHERE p.deletedAt IS NULL")
    Page<Parent> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Parent p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Parent> findActiveById(@Param("id") Long id);
}