package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Attendance;
import com.hopeuniversity.hope_university_management.domain.entities.ClassSession;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.enums.AttendanceStatus;
import com.hopeuniversity.hope_university_management.domain.repositories.AttendanceRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.ClassSessionRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.AttendanceRequest;
import com.hopeuniversity.hope_university_management.dto.response.AttendanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ClassSessionRepository classSessionRepository;

    public Page<AttendanceResponse> getAllAttendance(Pageable pageable) {
        return attendanceRepository.findAll(pageable).map(this::toResponse);
    }

    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        return toResponse(attendance);
    }

    public Page<AttendanceResponse> getAttendanceByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return attendanceRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    public Page<AttendanceResponse> getAttendanceByClassSession(Long classSessionId, Pageable pageable) {
        ClassSession classSession = classSessionRepository.findById(classSessionId)
                .orElseThrow(() -> new RuntimeException("Class session not found"));
        return attendanceRepository.findByClassSession(classSession, pageable).map(this::toResponse);
    }

    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        ClassSession classSession = classSessionRepository.findById(request.getClassSessionId())
                .orElseThrow(() -> new RuntimeException("Class session not found"));

        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();

        // Check if attendance already exists for this student, class session, and date
        Attendance existing = attendanceRepository
                .findByStudentAndClassSessionAndDate(student, classSession, date)
                .orElse(null);

        Attendance attendance;
        if (existing != null) {
            // Update existing record
            existing.setStatus(request.getStatus());
            existing.setRemarks(request.getRemarks());
            attendance = attendanceRepository.save(existing);
        } else {
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setClassSession(classSession);
            attendance.setStatus(request.getStatus());
            attendance.setDate(date);
            attendance.setRemarks(request.getRemarks());
            attendance = attendanceRepository.save(attendance);
        }

        return toResponse(attendance);
    }

    @Transactional
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    private AttendanceResponse toResponse(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getStudent().getId(),
                attendance.getStudent().getUser().getFullName(),
                attendance.getStudent().getUser().getEmail(),
                attendance.getClassSession().getId(),
                attendance.getClassSession().getCourse().getCode(),
                attendance.getClassSession().getCourse().getName(),
                attendance.getClassSession().getInstructor().getUser().getFullName(),
                attendance.getStatus(),
                attendance.getDate(),
                attendance.getRemarks(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt()
        );
    }
}