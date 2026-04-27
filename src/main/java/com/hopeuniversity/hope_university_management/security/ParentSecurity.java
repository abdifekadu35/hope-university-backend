package com.hopeuniversity.hope_university_management.security;

import com.hopeuniversity.hope_university_management.domain.entities.Parent;
import com.hopeuniversity.hope_university_management.domain.repositories.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("parentSecurity")
@RequiredArgsConstructor
public class ParentSecurity {

    private final ParentRepository parentRepository;

    public boolean isParentOwner(Long parentId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();
        return parentRepository.findById(parentId)
                .map(parent -> parent.getUser().getId().equals(userId))
                .orElse(false);
    }
}