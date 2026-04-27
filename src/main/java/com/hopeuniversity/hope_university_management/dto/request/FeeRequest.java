package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeeRequest {
    @NotNull
    private Long studentId;

    private String description;

    @NotNull
    private Double amount;

    private LocalDate dueDate;

    private Boolean isMandatory = true;
}