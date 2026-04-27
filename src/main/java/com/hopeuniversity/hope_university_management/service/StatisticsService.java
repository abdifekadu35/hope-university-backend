package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.enums.AttendanceStatus;
import com.hopeuniversity.hope_university_management.domain.enums.EnrollmentStatus;
import com.hopeuniversity.hope_university_management.domain.enums.PaymentStatus;
import com.hopeuniversity.hope_university_management.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final FeeRepository feeRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count only active (non‑deleted) records
        stats.put("totalUsers", userRepository.count()); // User may not have soft delete; adjust if needed
        stats.put("totalStudents", studentRepository.countActive());        // needs custom method
        stats.put("totalInstructors", instructorRepository.countActive());
        stats.put("totalCourses", courseRepository.countActive());
        stats.put("totalBooks", bookRepository.count()); // Book may not have soft delete

        // Enrollments (assuming Enrollment has no soft delete; use status filter)
        stats.put("totalEnrollments", enrollmentRepository.countByStatus(EnrollmentStatus.ENROLLED));
        stats.put("activeEnrollments", enrollmentRepository.countByStatus(EnrollmentStatus.ENROLLED)); // or same

        // Attendance (unchanged, but ensure methods exist)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long presentCount = attendanceRepository.countByStatusAndDateAfter(AttendanceStatus.PRESENT, thirtyDaysAgo);
        long absentCount = attendanceRepository.countByStatusAndDateAfter(AttendanceStatus.ABSENT, thirtyDaysAgo);
        stats.put("attendancePresentLast30Days", presentCount);
        stats.put("attendanceAbsentLast30Days", absentCount);

        // Payments and fees
        Double totalCollected = paymentRepository.sumAmountPaidByStatus(PaymentStatus.PAID);
        Double totalPendingFees = feeRepository.sumPendingFees();
        stats.put("totalRevenueCollected", totalCollected != null ? totalCollected : 0.0);
        stats.put("totalPendingFees", totalPendingFees != null ? totalPendingFees : 0.0);

        // Borrowing
        long booksBorrowed = borrowingRepository.countByIsReturnedFalse();
        stats.put("booksCurrentlyBorrowed", booksBorrowed);

        return stats;
    }

    public Map<String, Object> getGradeDistribution(Long courseId) {
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("A", 10);
        distribution.put("B", 20);
        distribution.put("C", 30);
        distribution.put("D", 15);
        distribution.put("F", 5);
        return distribution;
    }
}