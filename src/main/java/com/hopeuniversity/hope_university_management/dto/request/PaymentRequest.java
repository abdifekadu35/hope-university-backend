package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentRequest {
    @NotNull
    private Long studentId;

    private Long feeId; // optional

    @NotNull
    private Double amountPaid;

    private LocalDate paymentDate;

    private String paymentMethod;

    private String transactionReference;

    private String remarks;
}