package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassSessionRequest {
    @NotNull
    private Long courseId;

    @NotNull
    private Long instructorId;

    private Long roomId;

    @NotNull
    private Long timeSlotId;

    private String semester;
}