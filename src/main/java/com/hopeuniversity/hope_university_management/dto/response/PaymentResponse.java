package com.hopeuniversity.hope_university_management.dto.response;

import com.hopeuniversity.hope_university_management.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long feeId;
    private String feeDescription;
    private Double amountPaid;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String transactionReference;
    private PaymentStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}