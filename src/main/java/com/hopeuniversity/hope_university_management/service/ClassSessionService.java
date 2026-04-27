package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.ClassSession;
import com.hopeuniversity.hope_university_management.domain.entities.Course;
import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.entities.Room;
import com.hopeuniversity.hope_university_management.domain.entities.TimeSlot;
import com.hopeuniversity.hope_university_management.domain.repositories.ClassSessionRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.CourseRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.InstructorRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.RoomRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.TimeSlotRepository;
import com.hopeuniversity.hope_university_management.dto.request.ClassSessionRequest;
import com.hopeuniversity.hope_university_management.dto.response.ClassSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;

    public Page<ClassSessionResponse> getAllClassSessions(Pageable pageable) {
        return classSessionRepository.findAllActive(pageable).map(this::toResponse);
    }

    public ClassSessionResponse getClassSessionById(Long id) {
        ClassSession session = classSessionRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Class session not found"));
        return toResponse(session);
    }

    public Page<ClassSessionResponse> getClassSessionsByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return classSessionRepository.findByCourse(course, pageable).map(this::toResponse);
    }

    public Page<ClassSessionResponse> getClassSessionsByInstructor(Long instructorId, Pageable pageable) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return classSessionRepository.findByInstructor(instructor, pageable).map(this::toResponse);
    }

    @Transactional
    public ClassSessionResponse createClassSession(ClassSessionRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Instructor instructor = instructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
        }

        ClassSession session = new ClassSession();
        session.setCourse(course);
        session.setInstructor(instructor);
        session.setRoom(room);
        session.setTimeSlot(timeSlot);
        session.setSemester(request.getSemester());
        session.setStartTime(timeSlot.getStartTime());
        session.setEndTime(timeSlot.getEndTime());

        ClassSession saved = classSessionRepository.save(session);
        return toResponse(saved);
    }

    @Transactional
    public ClassSessionResponse updateClassSession(Long id, ClassSessionRequest request) {
        ClassSession session = classSessionRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Class session not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Instructor instructor = instructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        session.setCourse(course);
        session.setInstructor(instructor);
        session.setTimeSlot(timeSlot);
        session.setStartTime(timeSlot.getStartTime());
        session.setEndTime(timeSlot.getEndTime());
        session.setSemester(request.getSemester());

        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            session.setRoom(room);
        } else {
            session.setRoom(null);
        }

        ClassSession updated = classSessionRepository.save(session);
        return toResponse(updated);
    }

    @Transactional
    public void deleteClassSession(Long id) {
        classSessionRepository.findById(id).ifPresent(session -> {
            session.setDeletedAt(LocalDateTime.now());
            classSessionRepository.save(session);
        });
    }

    private ClassSessionResponse toResponse(ClassSession session) {
        return new ClassSessionResponse(
                session.getId(),
                session.getCourse().getId(),
                session.getCourse().getCode(),
                session.getCourse().getName(),
                session.getInstructor().getId(),
                session.getInstructor().getUser().getFullName(),
                session.getRoom() != null ? session.getRoom().getId() : null,
                session.getRoom() != null ? session.getRoom().getRoomNumber() : null,
                session.getTimeSlot().getId(),
                session.getTimeSlot().getDayOfWeek(),
                session.getStartTime(),
                session.getEndTime(),
                session.getSemester(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}