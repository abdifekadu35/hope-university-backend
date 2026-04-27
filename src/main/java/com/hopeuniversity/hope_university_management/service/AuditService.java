package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.AuditLog;
import com.hopeuniversity.hope_university_management.domain.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityType, Long entityId, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        String username = SecurityContextHolder.getContext().getAuthentication() != null ?
                SecurityContextHolder.getContext().getAuthentication().getName() : "system";
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        // IP address could be added via request interceptor; omitted for brevity
        auditLogRepository.save(log);
    }
}