package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.response.DocumentResponse;
import com.hopeuniversity.hope_university_management.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<Page<DocumentResponse>> getDocumentsByStudent(@PathVariable Long studentId,
                                                                        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentService.getDocumentsByStudent(studentId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @documentSecurity.canAccessDocument(#id, authentication)")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PostMapping("/upload/{studentId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @studentSecurity.isStudentOwner(#studentId, authentication)")
    public ResponseEntity<DocumentResponse> uploadDocument(@PathVariable Long studentId,
                                                           @RequestParam("file") MultipartFile file,
                                                           @RequestParam(value = "description", required = false) String description) throws IOException {
        return new ResponseEntity<>(documentService.uploadDocument(studentId, file, description), HttpStatus.CREATED);
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR') or @documentSecurity.canAccessDocument(#id, authentication)")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {
        Resource resource = documentService.downloadDocument(id);
        DocumentResponse docInfo = documentService.getDocumentById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(docInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docInfo.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<DocumentResponse> verifyDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.verifyDocument(id));
    }
}