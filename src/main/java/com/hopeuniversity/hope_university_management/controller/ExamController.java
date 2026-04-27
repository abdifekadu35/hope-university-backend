package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.ExamRequest;
import com.hopeuniversity.hope_university_management.dto.response.ExamResponse;
import com.hopeuniversity.hope_university_management.service.ExamService;
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
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<Page<ExamResponse>> getAllExams(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(examService.getAllExams(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'EXAM_OFFICER')")
    public ResponseEntity<Page<ExamResponse>> getExamsByCourse(@PathVariable Long courseId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(examService.getExamsByCourse(courseId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        return new ResponseEntity<>(examService.createExam(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXAM_OFFICER', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}