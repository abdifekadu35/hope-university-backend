package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long examId;

    private Double marksObtained;

    private String remarks;
}