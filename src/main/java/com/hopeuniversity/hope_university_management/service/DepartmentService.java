package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Department;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.repositories.DepartmentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.InstructorRepository;
import com.hopeuniversity.hope_university_management.dto.request.DepartmentRequest;
import com.hopeuniversity.hope_university_management.dto.response.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final InstructorRepository instructorRepository;

    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllActive(pageable).map(this::toResponse);
    }

    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return toResponse(department);
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists");
        }
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists");
        }

        Department department = new Department();
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());

        if (request.getHeadInstructorId() != null) {
            Instructor head = instructorRepository.findById(request.getHeadInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            department.setHeadInstructor(head);
        }

        Department saved = departmentRepository.save(department);
        return toResponse(saved);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!department.getName().equals(request.getName()) && departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists");
        }
        if (!department.getCode().equals(request.getCode()) && departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists");
        }

        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());

        if (request.getHeadInstructorId() != null) {
            Instructor head = instructorRepository.findById(request.getHeadInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            department.setHeadInstructor(head);
        } else {
            department.setHeadInstructor(null);
        }

        Department updated = departmentRepository.save(department);
        return toResponse(updated);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.findById(id).ifPresent(dept -> {
            dept.setDeletedAt(LocalDateTime.now());
            departmentRepository.save(dept);
        });
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getDescription(),
                department.getHeadInstructor() != null ? department.getHeadInstructor().getId() : null,
                department.getHeadInstructor() != null ? department.getHeadInstructor().getUser().getFullName() : null,
                department.getInstructors() != null ? department.getInstructors().size() : 0,
                department.getStudents() != null ? department.getStudents().size() : 0,
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}