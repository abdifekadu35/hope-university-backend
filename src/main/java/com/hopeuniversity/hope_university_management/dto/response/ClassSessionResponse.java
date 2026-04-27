package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionResponse {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long instructorId;
    private String instructorName;
    private Long roomId;
    private String roomNumber;
    private Long timeSlotId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String semester;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}