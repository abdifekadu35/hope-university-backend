package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.BorrowingRequest;
import com.hopeuniversity.hope_university_management.dto.response.BorrowingResponse;
import com.hopeuniversity.hope_university_management.service.BorrowingService;
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
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Page<BorrowingResponse>> getAllBorrowings(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(borrowingService.getAllBorrowings(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<BorrowingResponse> getBorrowingById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.getBorrowingById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<BorrowingResponse>> getBorrowingsByStudent(@PathVariable Long studentId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(borrowingService.getBorrowingsByStudent(studentId, pageable));
    }

    @PostMapping("/borrow")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<BorrowingResponse> borrowBook(@Valid @RequestBody BorrowingRequest request) {
        return new ResponseEntity<>(borrowingService.borrowBook(request), HttpStatus.CREATED);
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> processOverdueFines() {
        borrowingService.processOverdueFines();
        return ResponseEntity.accepted().build();
    }
}