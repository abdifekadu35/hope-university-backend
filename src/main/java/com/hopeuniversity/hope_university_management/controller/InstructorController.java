package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.response.BulkImportResult;
import com.hopeuniversity.hope_university_management.dto.request.InstructorRequest;
import com.hopeuniversity.hope_university_management.dto.response.InstructorResponse;
import com.hopeuniversity.hope_university_management.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    // Allow any authenticated user for now – change to proper role after testing
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<InstructorResponse>> getAllInstructors(Pageable pageable) {
        return ResponseEntity.ok(instructorService.getAllInstructors(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_REGISTRAR')")
    public ResponseEntity<InstructorResponse> createInstructor(@Valid @RequestBody InstructorRequest request) {
        return new ResponseEntity<>(instructorService.createInstructor(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_REGISTRAR')")
    public ResponseEntity<InstructorResponse> updateInstructor(@PathVariable Long id, @Valid @RequestBody InstructorRequest request) {
        return ResponseEntity.ok(instructorService.updateInstructor(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_REGISTRAR')")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    // Bulk import – also permissive for testing
    @PostMapping("/bulk")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BulkImportResult> bulkImportInstructors(@RequestParam("file") MultipartFile file) {
        try {
            BulkImportResult result = instructorService.bulkImportInstructors(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BulkImportResult(0, java.util.List.of(e.getMessage())));
        }
    }
}