package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Exam;
import com.hopeuniversity.hope_university_management.domain.entities.Grade;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.ExamRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.GradeRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.GradeRequest;
import com.hopeuniversity.hope_university_management.dto.response.GradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;

    public Page<GradeResponse> getAllGrades(Pageable pageable) {
        return gradeRepository.findAll(pageable).map(this::toResponse);
    }

    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return toResponse(grade);
    }

    public Page<GradeResponse> getGradesByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return gradeRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    public Page<GradeResponse> getGradesByExam(Long examId, Pageable pageable) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return gradeRepository.findByExam(exam, pageable).map(this::toResponse);
    }

    @Transactional
    public GradeResponse assignGrade(GradeRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Grade existing = gradeRepository.findByStudentAndExam(student, exam).orElse(null);
        Grade grade;
        if (existing != null) {
            existing.setMarksObtained(request.getMarksObtained());
            existing.setRemarks(request.getRemarks());
            existing.setLetterGrade(calculateLetterGrade(request.getMarksObtained(), exam.getMaxMarks()));
            grade = gradeRepository.save(existing);
        } else {
            grade = new Grade();
            grade.setStudent(student);
            grade.setExam(exam);
            grade.setMarksObtained(request.getMarksObtained());
            grade.setRemarks(request.getRemarks());
            grade.setLetterGrade(calculateLetterGrade(request.getMarksObtained(), exam.getMaxMarks()));
            grade = gradeRepository.save(grade);
        }
        return toResponse(grade);
    }

    @Transactional
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    private String calculateLetterGrade(Double marksObtained, Double maxMarks) {
        if (marksObtained == null || maxMarks == null || maxMarks == 0) return "N/A";
        double percentage = (marksObtained / maxMarks) * 100;
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private GradeResponse toResponse(Grade grade) {
        Exam exam = grade.getExam();
        Double maxMarks = exam.getMaxMarks();
        Double percentage = (grade.getMarksObtained() != null && maxMarks != null && maxMarks > 0)
                ? (grade.getMarksObtained() / maxMarks) * 100
                : null;

        return new GradeResponse(
                grade.getId(),
                grade.getStudent().getId(),
                grade.getStudent().getUser().getFullName(),
                grade.getStudent().getUser().getEmail(),
                exam.getId(),
                exam.getTitle(),
                exam.getCourse().getCode(),
                exam.getCourse().getName(),
                grade.getMarksObtained(),
                maxMarks,
                percentage,
                grade.getLetterGrade(),
                grade.getRemarks(),
                grade.getCreatedAt(),
                grade.getUpdatedAt()
        );
    }
}