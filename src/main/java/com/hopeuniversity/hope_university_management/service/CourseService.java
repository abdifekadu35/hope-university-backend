package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Department;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.repositories.CourseRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.DepartmentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.InstructorRepository;
import com.hopeuniversity.hope_university_management.dto.request.CourseRequest;
import com.hopeuniversity.hope_university_management.dto.response.BulkImportResult;
import com.hopeuniversity.hope_university_management.dto.response.CourseResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final InstructorRepository instructorRepository;

    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAllActive(pageable).map(this::toResponse);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return toResponse(course);
    }
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        // Check for duplicate name
        if (courseRepository.existsByName(request.getName())) {
            throw new RuntimeException("Course name already exists: " + request.getName());
        }

        // Auto‑generate course code if not provided
        String code = request.getCode();
        if (code == null || code.trim().isEmpty()) {
            code = generateCourseCode();
        } else {
            if (courseRepository.existsByCode(code)) {
                throw new RuntimeException("Course code already exists");
            }
        }

        Course course = new Course();
        course.setCode(code);
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDepartment(dept);
        }

        if (request.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            course.setInstructor(instructor);
        }

        Course saved = courseRepository.save(course);
        return toResponse(saved);   // <-- this return was missing
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // If code is provided and changed, check uniqueness
        if (request.getCode() != null && !request.getCode().isEmpty()) {
            if (!course.getCode().equals(request.getCode()) && courseRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Course code already exists");
            }
            course.setCode(request.getCode());
        }

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDepartment(dept);
        } else {
            course.setDepartment(null);
        }

        if (request.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            course.setInstructor(instructor);
        } else {
            course.setInstructor(null);
        }

        Course updated = courseRepository.save(course);
        return toResponse(updated);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setDeletedAt(LocalDateTime.now());
            courseRepository.save(course);
        });
    }

    // -------------------------------------------------------------------------
    // Auto‑generation of course code (CRS + current year + 4‑digit sequence)
    // -------------------------------------------------------------------------
    private String generateCourseCode() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "CRS" + year;
        List<Course> all = courseRepository.findAll(); // includes soft‑deleted? adjust if needed
        int maxNum = 0;
        for (Course c : all) {
            if (c.getCode() != null && c.getCode().startsWith(prefix)) {
                String numPart = c.getCode().substring(7); // after "CRSYYYY"
                try {
                    int num = Integer.parseInt(numPart);
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        int nextNum = maxNum + 1;
        return prefix + String.format("%04d", nextNum);
    }

    // -------------------------------------------------------------------------
    // Bulk import
    // -------------------------------------------------------------------------
    @Transactional
    public BulkImportResult bulkImportCourses(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        int created = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new RuntimeException("File is empty");
            }

            rows.next(); // skip header row

            List<CourseRequest> requests = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                try {
                    CourseRequest request = new CourseRequest();
                    // Columns: name, description, credits, departmentId, instructorId
                    request.setName(getCellValue(row, 0));
                    request.setDescription(getCellValue(row, 1));
                    String creditsStr = getCellValue(row, 2);
                    if (creditsStr != null && !creditsStr.isEmpty()) {
                        request.setCredits(Integer.parseInt(creditsStr));
                    }
                    String deptIdStr = getCellValue(row, 3);
                    if (deptIdStr != null && !deptIdStr.isEmpty()) {
                        request.setDepartmentId(Long.parseLong(deptIdStr));
                    }
                    String instrIdStr = getCellValue(row, 4);
                    if (instrIdStr != null && !instrIdStr.isEmpty()) {
                        request.setInstructorId(Long.parseLong(instrIdStr));
                    }
                    // code is left null → backend will auto‑generate
                    requests.add(request);
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException("Validation errors: " + String.join("; ", errors));
            }

            for (CourseRequest req : requests) {
                try {
                    createCourse(req);  // auto‑generates code if missing
                    created++;
                } catch (Exception e) {
                    errors.add("Failed for " + req.getName() + ": " + e.getMessage());
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

    // -------------------------------------------------------------------------
    // Helper mapping
    // -------------------------------------------------------------------------
    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getCredits(),
                course.getDepartment() != null ? course.getDepartment().getId() : null,
                course.getDepartment() != null ? course.getDepartment().getName() : null,
                course.getInstructor() != null ? course.getInstructor().getId() : null,
                course.getInstructor() != null ? course.getInstructor().getUser().getFullName() : null,
                course.getEnrollments() != null ? course.getEnrollments().size() : 0,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}