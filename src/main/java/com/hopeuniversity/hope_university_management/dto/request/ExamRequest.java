package com.hopeuniversity.hope_university_management.dto.request;

import com.hopeuniversity.hope_university_management.domain.enums.ExamType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExamRequest {
    @NotNull
    private Long courseId;

    private String title;

    @NotNull
    private ExamType examType;

    private Double maxMarks;

    private LocalDate examDate;

    private String description;
}