package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.ParentRequest;
import com.hopeuniversity.hope_university_management.dto.response.ParentResponse;
import com.hopeuniversity.hope_university_management.service.ParentService;
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

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Page<ParentResponse>> getAllParents(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(parentService.getAllParents(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL') or @parentSecurity.isParentOwner(#id, authentication)")
    public ResponseEntity<ParentResponse> getParentById(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getParentById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PARENT')")
    public ResponseEntity<ParentResponse> getMyParentProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Find user by email, then get parent profile
        // We'll add a method in service to get by email, or reuse getParentByUserId after fetching user.
        // For simplicity, let's add a helper method in service: getParentByEmail(String email)
        // I'll include it below.
        return ResponseEntity.ok(parentService.getParentByEmail(userDetails.getUsername()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ParentResponse> createParent(@Valid @RequestBody ParentRequest request) {
        return new ResponseEntity<>(parentService.createParent(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL') or @parentSecurity.isParentOwner(#id, authentication)")
    public ResponseEntity<ParentResponse> updateParent(@PathVariable Long id, @Valid @RequestBody ParentRequest request) {
        return ResponseEntity.ok(parentService.updateParent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }
}