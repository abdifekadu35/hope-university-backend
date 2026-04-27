package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StatisticsService statisticsService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(statisticsService.getDashboardStats());
    }

    @GetMapping("/grades/distribution/{courseId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<Map<String, Object>> getGradeDistribution(@PathVariable Long courseId) {
        return ResponseEntity.ok(statisticsService.getGradeDistribution(courseId));
    }
}