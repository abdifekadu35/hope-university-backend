package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Exam;
import com.hopeuniversity.hope_university_management.domain.repositories.CourseRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.ExamRepository;
import com.hopeuniversity.hope_university_management.dto.request.ExamRequest;
import com.hopeuniversity.hope_university_management.dto.response.ExamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;

    public Page<ExamResponse> getAllExams(Pageable pageable) {
        return examRepository.findAllActive(pageable).map(this::toResponse);
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return toResponse(exam);
    }

    public Page<ExamResponse> getExamsByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return examRepository.findByCourse(course, pageable).map(this::toResponse);
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Exam exam = new Exam();
        exam.setCourse(course);
        exam.setTitle(request.getTitle());
        exam.setExamType(request.getExamType());
        exam.setMaxMarks(request.getMaxMarks());
        exam.setExamDate(request.getExamDate());
        exam.setDescription(request.getDescription());

        Exam saved = examRepository.save(exam);
        return toResponse(saved);
    }

    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        exam.setCourse(course);
        exam.setTitle(request.getTitle());
        exam.setExamType(request.getExamType());
        exam.setMaxMarks(request.getMaxMarks());
        exam.setExamDate(request.getExamDate());
        exam.setDescription(request.getDescription());

        Exam updated = examRepository.save(exam);
        return toResponse(updated);
    }

    @Transactional
    public void deleteExam(Long id) {
        examRepository.findById(id).ifPresent(exam -> {
            exam.setDeletedAt(LocalDateTime.now());
            examRepository.save(exam);
        });
    }

    private ExamResponse toResponse(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getCourse().getId(),
                exam.getCourse().getCode(),
                exam.getCourse().getName(),
                exam.getTitle(),
                exam.getExamType(),
                exam.getMaxMarks(),
                exam.getExamDate(),
                exam.getDescription(),
                exam.getCreatedAt(),
                exam.getUpdatedAt()
        );
    }
}