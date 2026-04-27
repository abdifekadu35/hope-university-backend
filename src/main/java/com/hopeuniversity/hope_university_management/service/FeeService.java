package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Fee;
import com.hopeuniversity.hope_university_management.domain.entities.Payment;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.FeeRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.PaymentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.FeeRequest;
import com.hopeuniversity.hope_university_management.dto.response.FeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    public Page<FeeResponse> getAllFees(Pageable pageable) {
        return feeRepository.findAllActive(pageable).map(this::toResponse);
    }

    public FeeResponse getFeeById(Long id) {
        Fee fee = feeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Fee record not found"));
        return toResponse(fee);
    }

    public Page<FeeResponse> getFeesByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return feeRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    @Transactional
    public FeeResponse createFee(FeeRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setDescription(request.getDescription());
        fee.setAmount(request.getAmount());
        fee.setDueDate(request.getDueDate());
        fee.setIsMandatory(request.getIsMandatory());

        Fee saved = feeRepository.save(fee);
        return toResponse(saved);
    }

    @Transactional
    public FeeResponse updateFee(Long id, FeeRequest request) {
        Fee fee = feeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Fee record not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        fee.setStudent(student);
        fee.setDescription(request.getDescription());
        fee.setAmount(request.getAmount());
        fee.setDueDate(request.getDueDate());
        fee.setIsMandatory(request.getIsMandatory());

        Fee updated = feeRepository.save(fee);
        return toResponse(updated);
    }

    @Transactional
    public void deleteFee(Long id) {
        feeRepository.findById(id).ifPresent(fee -> {
            fee.setDeletedAt(LocalDate.now().atStartOfDay());
            feeRepository.save(fee);
        });
    }

    private FeeResponse toResponse(Fee fee) {
        // Calculate total paid for this fee
        List<Payment> payments = paymentRepository.findByStudent(fee.getStudent(), Pageable.unpaged())
                .stream().filter(p -> p.getFee() != null && p.getFee().getId().equals(fee.getId()))
                .toList();
        double totalPaid = payments.stream().mapToDouble(Payment::getAmountPaid).sum();
        double balance = fee.getAmount() - totalPaid;
        String status;
        if (balance <= 0) status = "PAID";
        else if (totalPaid > 0) status = "PARTIAL";
        else if (fee.getDueDate() != null && fee.getDueDate().isBefore(LocalDate.now())) status = "OVERDUE";
        else status = "PENDING";

        return new FeeResponse(
                fee.getId(),
                fee.getStudent().getId(),
                fee.getStudent().getUser().getFullName(),
                fee.getStudent().getUser().getEmail(),
                fee.getDescription(),
                fee.getAmount(),
                fee.getDueDate(),
                fee.getIsMandatory(),
                totalPaid,
                balance,
                status,
                fee.getCreatedAt(),
                fee.getUpdatedAt()
        );
    }
}