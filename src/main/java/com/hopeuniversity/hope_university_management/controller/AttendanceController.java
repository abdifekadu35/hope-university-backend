package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.AttendanceRequest;
import com.hopeuniversity.hope_university_management.dto.response.AttendanceResponse;
import com.hopeuniversity.hope_university_management.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Page<AttendanceResponse>> getAllAttendance(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getAllAttendance(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<AttendanceResponse>> getAttendanceByStudent(@PathVariable Long studentId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId, pageable));
    }

    @GetMapping("/class-session/{classSessionId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<Page<AttendanceResponse>> getAttendanceByClassSession(@PathVariable Long classSessionId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClassSession(classSessionId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        return new ResponseEntity<>(attendanceService.markAttendance(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}