package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.TimeSlot;
import com.hopeuniversity.hope_university_management.domain.repositories.TimeSlotRepository;
import com.hopeuniversity.hope_university_management.dto.request.TimeSlotRequest;
import com.hopeuniversity.hope_university_management.dto.response.TimeSlotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public Page<TimeSlotResponse> getAllTimeSlots(Pageable pageable) {
        return timeSlotRepository.findAllActive(pageable).map(this::toResponse);
    }

    public TimeSlotResponse getTimeSlotById(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        return toResponse(timeSlot);
    }

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDayOfWeek(request.getDayOfWeek());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setDescription(request.getDescription());
        TimeSlot saved = timeSlotRepository.save(timeSlot);
        return toResponse(saved);
    }

    @Transactional
    public TimeSlotResponse updateTimeSlot(Long id, TimeSlotRequest request) {
        TimeSlot timeSlot = timeSlotRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        timeSlot.setDayOfWeek(request.getDayOfWeek());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setDescription(request.getDescription());
        TimeSlot updated = timeSlotRepository.save(timeSlot);
        return toResponse(updated);
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        timeSlotRepository.findById(id).ifPresent(slot -> {
            slot.setDeletedAt(LocalDateTime.now());
            timeSlotRepository.save(slot);
        });
    }

    private TimeSlotResponse toResponse(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.getId(),
                timeSlot.getDayOfWeek(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                timeSlot.getDescription(),
                timeSlot.getCreatedAt(),
                timeSlot.getUpdatedAt()
        );
    }
}