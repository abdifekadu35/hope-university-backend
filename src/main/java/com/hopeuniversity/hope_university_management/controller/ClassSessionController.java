package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.ClassSessionRequest;
import com.hopeuniversity.hope_university_management.dto.response.ClassSessionResponse;
import com.hopeuniversity.hope_university_management.service.ClassSessionService;
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
@RequestMapping("/api/v1/class-sessions")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService classSessionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<ClassSessionResponse>> getAllClassSessions(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(classSessionService.getAllClassSessions(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ClassSessionResponse> getClassSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(classSessionService.getClassSessionById(id));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<ClassSessionResponse>> getClassSessionsByCourse(@PathVariable Long courseId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(classSessionService.getClassSessionsByCourse(courseId, pageable));
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<Page<ClassSessionResponse>> getClassSessionsByInstructor(@PathVariable Long instructorId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(classSessionService.getClassSessionsByInstructor(instructorId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<ClassSessionResponse> createClassSession(@Valid @RequestBody ClassSessionRequest request) {
        return new ResponseEntity<>(classSessionService.createClassSession(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<ClassSessionResponse> updateClassSession(@PathVariable Long id, @Valid @RequestBody ClassSessionRequest request) {
        return ResponseEntity.ok(classSessionService.updateClassSession(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Void> deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
        return ResponseEntity.noContent().build();
    }
}