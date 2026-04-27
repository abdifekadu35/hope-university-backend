package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final AuditLogRepository auditLogRepository;

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("totalAuditLogs", auditLogRepository.count());
        // Could add DB connection check, memory usage, etc.
        return health;
    }
}