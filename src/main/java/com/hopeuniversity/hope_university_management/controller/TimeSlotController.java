package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.TimeSlotRequest;
import com.hopeuniversity.hope_university_management.dto.response.TimeSlotResponse;
import com.hopeuniversity.hope_university_management.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<Page<TimeSlotResponse>> getAllTimeSlots(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(timeSlotService.getAllTimeSlots(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(@PathVariable Long id) {
        return ResponseEntity.ok(timeSlotService.getTimeSlotById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@Valid @RequestBody TimeSlotRequest request) {
        return new ResponseEntity<>(timeSlotService.createTimeSlot(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<TimeSlotResponse> updateTimeSlot(@PathVariable Long id, @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(timeSlotService.updateTimeSlot(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'REGISTRAR')")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}