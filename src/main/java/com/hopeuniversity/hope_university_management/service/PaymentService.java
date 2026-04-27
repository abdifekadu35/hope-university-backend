package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Fee;
import com.hopeuniversity.hope_university_management.domain.entities.Payment;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.enums.PaymentStatus;
import com.hopeuniversity.hope_university_management.domain.repositories.FeeRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.PaymentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.PaymentRequest;
import com.hopeuniversity.hope_university_management.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final FeeRepository feeRepository;

    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::toResponse);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponse(payment);
    }

    public Page<PaymentResponse> getPaymentsByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return paymentRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Fee fee = null;
        if (request.getFeeId() != null) {
            fee = feeRepository.findById(request.getFeeId())
                    .orElseThrow(() -> new RuntimeException("Fee record not found"));
        }

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setFee(fee);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setStatus(PaymentStatus.PAID);
        payment.setRemarks(request.getRemarks());

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getStudent().getId(),
                payment.getStudent().getUser().getFullName(),
                payment.getStudent().getUser().getEmail(),
                payment.getFee() != null ? payment.getFee().getId() : null,
                payment.getFee() != null ? payment.getFee().getDescription() : null,
                payment.getAmountPaid(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getTransactionReference(),
                payment.getStatus(),
                payment.getRemarks(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}