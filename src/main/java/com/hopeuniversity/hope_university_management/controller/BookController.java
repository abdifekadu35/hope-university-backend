package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.BookRequest;
import com.hopeuniversity.hope_university_management.dto.response.BookResponse;
import com.hopeuniversity.hope_university_management.service.BookService;
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
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<BookResponse>> getAllBooks(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<BookResponse>> searchBooks(@RequestParam String keyword, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return new ResponseEntity<>(bookService.createBook(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}