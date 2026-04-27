package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Department;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.entities.Role;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.RoleName;
import com.hopeuniversity.hope_university_management.domain.repositories.DepartmentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.InstructorRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.RoleRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import com.hopeuniversity.hope_university_management.dto.request.InstructorRequest;
import com.hopeuniversity.hope_university_management.dto.response.BulkImportResult;
import com.hopeuniversity.hope_university_management.dto.response.InstructorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<InstructorResponse> getAllInstructors(Pageable pageable) {
        return instructorRepository.findAllActive(pageable).map(this::toResponse);
    }

    public InstructorResponse getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return toResponse(instructor);
    }

    public InstructorResponse getInstructorByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Instructor profile not found"));
        return toResponse(instructor);
    }

    @Transactional
    public InstructorResponse createInstructor(InstructorRequest request) {
        String instructorId = request.getInstructorId();
        if (instructorId == null || instructorId.trim().isEmpty()) {
            instructorId = generateInstructorId();
        } else {
            if (instructorRepository.existsByInstructorId(instructorId)) {
                throw new RuntimeException("Instructor ID already exists");
            }
        }

        User user;
        if (request.getUserId() != null) {
            user = userRepository.findActiveById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getRole().getName() != RoleName.TEACHER) {
                throw new RuntimeException("User does not have TEACHER role");
            }
        } else {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            Role teacherRole = roleRepository.findByName(RoleName.TEACHER)
                    .orElseThrow(() -> new RuntimeException("TEACHER role not found"));
            user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(teacherRole);
            user.setActive(true);
            user = userRepository.save(user);
        }

        Instructor instructor = new Instructor();
        instructor.setInstructorId(instructorId);
        instructor.setUser(user);
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            instructor.setDepartment(dept);
        }
        instructor.setOffice(request.getOffice());
        instructor.setPhone(request.getPhone());
        instructor.setHireDate(request.getHireDate());
        instructor.setTitle(request.getTitle());

        Instructor saved = instructorRepository.save(instructor);
        return toResponse(saved);
    }

    @Transactional
    public InstructorResponse updateInstructor(Long id, InstructorRequest request) {
        Instructor instructor = instructorRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        if (request.getInstructorId() != null && !request.getInstructorId().isEmpty()) {
            if (!request.getInstructorId().equals(instructor.getInstructorId()) &&
                    instructorRepository.existsByInstructorId(request.getInstructorId())) {
                throw new RuntimeException("Instructor ID already exists");
            }
            instructor.setInstructorId(request.getInstructorId());
        }

        instructor.setOffice(request.getOffice());
        instructor.setPhone(request.getPhone());
        instructor.setHireDate(request.getHireDate());
        instructor.setTitle(request.getTitle());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            instructor.setDepartment(dept);
        } else {
            instructor.setDepartment(null);
        }

        User user = instructor.getUser();
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

        Instructor updated = instructorRepository.save(instructor);
        return toResponse(updated);
    }

    @Transactional
    public void deleteInstructor(Long id) {
        instructorRepository.findById(id).ifPresent(instructor -> {
            instructor.setDeletedAt(LocalDateTime.now());
            instructorRepository.save(instructor);
        });
    }

    private String generateInstructorId() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "INS" + year;
        List<Instructor> all = instructorRepository.findAll();
        int maxNum = 0;
        for (Instructor i : all) {
            if (i.getInstructorId() != null && i.getInstructorId().startsWith(prefix)) {
                String numPart = i.getInstructorId().substring(7);
                try {
                    int num = Integer.parseInt(numPart);
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        int nextNum = maxNum + 1;
        return prefix + String.format("%04d", nextNum);
    }

    @Transactional
    public BulkImportResult bulkImportInstructors(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        int created = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new RuntimeException("File is empty");
            }

            rows.next(); // skip header

            List<InstructorRequest> requests = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                try {
                    InstructorRequest request = new InstructorRequest();
                    request.setFullName(getCellValue(row, 0));
                    request.setEmail(getCellValue(row, 1));
                    request.setPassword(getCellValue(row, 2));
                    String deptIdStr = getCellValue(row, 3);
                    if (deptIdStr != null && !deptIdStr.isEmpty()) {
                        request.setDepartmentId(Long.parseLong(deptIdStr));
                    }
                    request.setOffice(getCellValue(row, 4));
                    request.setPhone(getCellValue(row, 5));
                    String hireDateStr = getCellValue(row, 6);
                    if (hireDateStr != null && !hireDateStr.isEmpty()) {
                        request.setHireDate(LocalDateTime.parse(hireDateStr + "T00:00:00"));
                    }
                    request.setTitle(getCellValue(row, 7));
                    requests.add(request);
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException("Validation errors: " + String.join("; ", errors));
            }

            for (InstructorRequest req : requests) {
                try {
                    createInstructor(req);
                    created++;
                } catch (Exception e) {
                    errors.add("Failed for " + req.getEmail() + ": " + e.getMessage());
                    throw new RuntimeException("Transaction rolled back: " + String.join("; ", errors));
                }
            }

            return new BulkImportResult(created, errors);
        } catch (Exception e) {
            throw new RuntimeException("Bulk import failed: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private InstructorResponse toResponse(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getInstructorId(),
                instructor.getUser().getId(),
                instructor.getUser().getFullName(),
                instructor.getUser().getEmail(),
                instructor.getDepartment() != null ? instructor.getDepartment().getId() : null,
                instructor.getDepartment() != null ? instructor.getDepartment().getName() : null,
                instructor.getOffice(),
                instructor.getPhone(),
                instructor.getHireDate(),
                instructor.getTitle(),
                instructor.getCreatedAt(),
                instructor.getUpdatedAt()
        );
    }
}