package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.domain.entities.AuditLog;
import com.hopeuniversity.hope_university_management.domain.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('IT_SUPPORT', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<AuditLog>> getAllLogs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogRepository.findAll(pageable));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyAuthority('IT_SUPPORT', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<AuditLog>> getLogsByUser(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(auditLogRepository.findByUsername(username, pageable));
    }

    @GetMapping("/entity/{entityType}")
    @PreAuthorize("hasAnyAuthority('IT_SUPPORT', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<AuditLog>> getLogsByEntityType(@PathVariable String entityType, Pageable pageable) {
        return ResponseEntity.ok(auditLogRepository.findByEntityType(entityType, pageable));
    }
}