package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String description;
    private Double amount;
    private LocalDate dueDate;
    private Boolean isMandatory;
    private Double totalPaid; // computed: sum of payments against this fee
    private Double balance;
    private String status; // PAID, PARTIAL, PENDING, OVERDUE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}