package com.hopeuniversity.hope_university_management.security;

import com.hopeuniversity.hope_university_management.domain.entities.Document;
import com.hopeuniversity.hope_university_management.domain.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("documentSecurity")
@RequiredArgsConstructor
public class DocumentSecurity {

    private final DocumentRepository documentRepository;

    public boolean canAccessDocument(Long documentId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();

        Document document = documentRepository.findById(documentId).orElse(null);
        if (document == null) return false;

        // Check if the authenticated user is the student who owns the document
        Long studentUserId = document.getStudent().getUser().getId();
        if (studentUserId.equals(userId)) return true;

        // Check if the user is a parent of the student
        // This would require parent-child relationship check – can be added later.
        return false;
    }
}