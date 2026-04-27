package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Document;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.DocumentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.response.DocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public DocumentResponse uploadDocument(Long studentId, MultipartFile file, String description) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String storedFileName = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setStudent(student);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(storedFileName);
        document.setDescription(description);
        document.setIsVerified(false);

        Document saved = documentRepository.save(document);
        return toResponse(saved);
    }

    public Page<DocumentResponse> getDocumentsByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return documentRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return toResponse(document);
    }

    public Resource downloadDocument(Long id) throws IOException {
        Document document = documentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return fileStorageService.loadFileAsResource(document.getFilePath());
    }

    @Transactional
    public void deleteDocument(Long id) throws IOException {
        Document document = documentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        fileStorageService.deleteFile(document.getFilePath());
        documentRepository.softDeleteById(id);
    }

    @Transactional
    public DocumentResponse verifyDocument(Long id) {
        Document document = documentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        document.setIsVerified(true);
        Document updated = documentRepository.save(document);
        return toResponse(updated);
    }

    private DocumentResponse toResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getStudent().getId(),
                doc.getStudent().getUser().getFullName(),
                doc.getFileName(),
                doc.getFileType(),
                doc.getFileSize(),
                doc.getDescription(),
                doc.getIsVerified(),
                doc.getCreatedAt()
        );
    }
}