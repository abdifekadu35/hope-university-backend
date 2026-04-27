package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.StudentRequest;
import com.hopeuniversity.hope_university_management.dto.response.StudentResponse;
import com.hopeuniversity.hope_university_management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;


@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR')")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR') or @studentSecurity.isStudentOwner(#id, authentication)")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<StudentResponse> getMyStudentProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(studentService.getStudentByEmail(userDetails.getUsername()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR')")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR') or @studentSecurity.isStudentOwner(#id, authentication)")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR')")
    public ResponseEntity<Map<String, Object>> bulkImportStudents(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = studentService.bulkImport(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("created", 0);
            errorResult.put("errors", List.of(e.getMessage()));
            return ResponseEntity.ok(errorResult); // still 200 OK
        }
    }
    @GetMapping("/{id}/id-card")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'REGISTRAR') or @studentSecurity.isStudentOwner(#id, authentication)")
    public ResponseEntity<Resource> generateIdCard(@PathVariable Long id) {
        try {
            ByteArrayInputStream in = studentService.generateIdCard(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=student_id_card_" + id + ".pdf")
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}