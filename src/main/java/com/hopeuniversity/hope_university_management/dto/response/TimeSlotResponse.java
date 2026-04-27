package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponse {
    private Long id;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}