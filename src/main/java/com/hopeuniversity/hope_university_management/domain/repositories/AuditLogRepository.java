package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUsername(String username, Pageable pageable);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
}