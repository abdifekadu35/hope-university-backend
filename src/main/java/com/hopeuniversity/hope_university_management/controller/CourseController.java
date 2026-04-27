package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.CourseRequest;
import com.hopeuniversity.hope_university_management.dto.response.BulkImportResult;
import com.hopeuniversity.hope_university_management.dto.response.CourseResponse;
import com.hopeuniversity.hope_university_management.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<CourseResponse>> getAllCourses(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return new ResponseEntity<>(courseService.createCourse(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN') or hasAuthority('REGISTRAR')")
    public ResponseEntity<BulkImportResult> bulkImportCourses(@RequestParam("file") MultipartFile file) {
        try {
            BulkImportResult result = courseService.bulkImportCourses(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BulkImportResult(0, List.of(e.getMessage())));
        }
    }
}