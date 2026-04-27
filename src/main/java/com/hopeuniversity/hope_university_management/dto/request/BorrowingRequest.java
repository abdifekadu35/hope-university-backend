package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowingRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long bookId;

    private LocalDate issueDate; // defaults to today if not provided

    private LocalDate dueDate;   // defaults to issueDate + 14 days if not provided
}