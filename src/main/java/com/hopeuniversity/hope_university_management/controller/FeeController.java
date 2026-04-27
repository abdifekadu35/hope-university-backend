package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.FeeRequest;
import com.hopeuniversity.hope_university_management.dto.response.FeeResponse;
import com.hopeuniversity.hope_university_management.service.FeeService;
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
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<Page<FeeResponse>> getAllFees(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(feeService.getAllFees(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<FeeResponse> getFeeById(@PathVariable Long id) {
        return ResponseEntity.ok(feeService.getFeeById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<FeeResponse>> getFeesByStudent(@PathVariable Long studentId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(feeService.getFeesByStudent(studentId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<FeeResponse> createFee(@Valid @RequestBody FeeRequest request) {
        return new ResponseEntity<>(feeService.createFee(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<FeeResponse> updateFee(@PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        return ResponseEntity.ok(feeService.updateFee(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<Void> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        return ResponseEntity.noContent().build();
    }
}