package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.GradeRequest;
import com.hopeuniversity.hope_university_management.dto.response.GradeResponse;
import com.hopeuniversity.hope_university_management.service.GradeService;
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
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<Page<GradeResponse>> getAllGrades(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(gradeService.getAllGrades(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<GradeResponse> getGradeById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<GradeResponse>> getGradesByStudent(@PathVariable Long studentId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId, pageable));
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<Page<GradeResponse>> getGradesByExam(@PathVariable Long examId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(gradeService.getGradesByExam(examId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<GradeResponse> assignGrade(@Valid @RequestBody GradeRequest request) {
        return new ResponseEntity<>(gradeService.assignGrade(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}