package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TimeSlotRequest {
    @NotBlank
    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private String description;
}