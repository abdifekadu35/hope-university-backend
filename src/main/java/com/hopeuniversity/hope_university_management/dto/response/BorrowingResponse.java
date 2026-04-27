package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double fineAmount;
    private Boolean isReturned;
    private String remarks;
    private LocalDateTime createdAt;
}