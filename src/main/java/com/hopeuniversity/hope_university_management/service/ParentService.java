package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Parent;
import com.hopeuniversity.hope_university_management.domain.entities.Role;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.RoleName;
import com.hopeuniversity.hope_university_management.domain.repositories.ParentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.RoleRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import com.hopeuniversity.hope_university_management.dto.request.ParentRequest;
import com.hopeuniversity.hope_university_management.dto.response.ParentResponse;
import com.hopeuniversity.hope_university_management.dto.response.StudentSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<ParentResponse> getAllParents(Pageable pageable) {
        return parentRepository.findAllActive(pageable).map(this::toResponse);
    }

    public ParentResponse getParentById(Long id) {
        Parent parent = parentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        return toResponse(parent);
    }

    public ParentResponse getParentByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));
        return toResponse(parent);
    }

    public ParentResponse getParentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));
        return toResponse(parent);
    }

    @Transactional
    public ParentResponse createParent(ParentRequest request) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findActiveById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getRole().getName() != RoleName.PARENT) {
                throw new RuntimeException("User does not have PARENT role");
            }
        } else {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            Role parentRole = roleRepository.findByName(RoleName.PARENT)
                    .orElseThrow(() -> new RuntimeException("PARENT role not found"));
            user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(parentRole);
            user.setActive(true);
            user = userRepository.save(user);
        }

        Parent parent = new Parent();
        parent.setUser(user);
        parent.setPhone(request.getPhone());
        parent.setAddress(request.getAddress());
        parent.setOccupation(request.getOccupation());

        if (request.getChildStudentIds() != null && !request.getChildStudentIds().isEmpty()) {
            List<Student> children = studentRepository.findAllById(request.getChildStudentIds());
            parent.setChildren(children);
        }

        Parent saved = parentRepository.save(parent);
        return toResponse(saved);
    }

    @Transactional
    public ParentResponse updateParent(Long id, ParentRequest request) {
        Parent parent = parentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        parent.setPhone(request.getPhone());
        parent.setAddress(request.getAddress());
        parent.setOccupation(request.getOccupation());

        if (request.getChildStudentIds() != null) {
            List<Student> children = studentRepository.findAllById(request.getChildStudentIds());
            parent.setChildren(children);
        }

        // Update linked user if new info provided
        User user = parent.getUser();
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);

        Parent updated = parentRepository.save(parent);
        return toResponse(updated);
    }

    @Transactional
    public void deleteParent(Long id) {
        parentRepository.findById(id).ifPresent(parent -> {
            parent.setDeletedAt(LocalDateTime.now());
            parentRepository.save(parent);
        });
    }

    private ParentResponse toResponse(Parent parent) {
        List<StudentSimpleResponse> childrenResponses = parent.getChildren().stream()
                .map(student -> new StudentSimpleResponse(
                        student.getId(),
                        student.getStudentId(),
                        student.getUser().getFullName(),
                        student.getUser().getEmail(),
                        student.getDepartment() != null ? student.getDepartment().getName() : null
                ))
                .collect(Collectors.toList());

        return new ParentResponse(
                parent.getId(),
                parent.getUser().getId(),
                parent.getUser().getFullName(),
                parent.getUser().getEmail(),
                parent.getPhone(),
                parent.getAddress(),
                parent.getOccupation(),
                childrenResponses,
                parent.getCreatedAt(),
                parent.getUpdatedAt()
        );
    }
}