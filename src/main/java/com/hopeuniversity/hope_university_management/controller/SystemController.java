package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/health")
    @PreAuthorize("hasAnyAuthority('IT_SUPPORT', 'SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(systemService.getSystemHealth());
    }
}