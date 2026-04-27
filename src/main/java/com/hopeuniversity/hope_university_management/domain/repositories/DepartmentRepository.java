package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    Optional<Department> findByCode(String code);

    @Query("SELECT d FROM Department d WHERE d.deletedAt IS NULL")
    Page<Department> findAllActive(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<Department> findActiveById(@Param("id") Long id);

    boolean existsByName(String name);

    boolean existsByCode(String code);
}