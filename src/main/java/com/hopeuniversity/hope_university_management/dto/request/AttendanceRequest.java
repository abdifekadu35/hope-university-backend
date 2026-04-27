package com.hopeuniversity.hope_university_management.dto.request;

import com.hopeuniversity.hope_university_management.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long classSessionId;

    @NotNull
    private AttendanceStatus status;

    private LocalDate date; // if null, use current date

    private String remarks;
}