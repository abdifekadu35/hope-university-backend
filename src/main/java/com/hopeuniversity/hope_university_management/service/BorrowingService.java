package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Book;
import com.hopeuniversity.hope_university_management.domain.entities.Borrowing;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.BookRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.BorrowingRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.dto.request.BorrowingRequest;
import com.hopeuniversity.hope_university_management.dto.response.BorrowingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    public Page<BorrowingResponse> getAllBorrowings(Pageable pageable) {
        return borrowingRepository.findAll(pageable).map(this::toResponse);
    }

    public BorrowingResponse getBorrowingById(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));
        return toResponse(borrowing);
    }

    public Page<BorrowingResponse> getBorrowingsByStudent(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return borrowingRepository.findByStudent(student, pageable).map(this::toResponse);
    }

    @Transactional
    public BorrowingResponse borrowBook(BorrowingRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Book book = bookRepository.findActiveById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of this book");
        }

        // Check if student already has an unreturned copy of this book
        if (borrowingRepository.findByStudentAndBookAndIsReturnedFalse(student, book).isPresent()) {
            throw new RuntimeException("Student already has an unreturned copy of this book");
        }

        LocalDate issueDate = request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now();
        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : issueDate.plusDays(14);

        Borrowing borrowing = new Borrowing();
        borrowing.setStudent(student);
        borrowing.setBook(book);
        borrowing.setIssueDate(issueDate);
        borrowing.setDueDate(dueDate);
        borrowing.setIsReturned(false);

        Borrowing saved = borrowingRepository.save(borrowing);

        // Decrement available copies
        bookRepository.decrementAvailableCopies(book.getId());

        return toResponse(saved);
    }

    @Transactional
    public BorrowingResponse returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));

        if (borrowing.getIsReturned()) {
            throw new RuntimeException("Book already returned");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;
        if (returnDate.isAfter(borrowing.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(borrowing.getDueDate(), returnDate);
            fine = daysLate * 5.0; // $5 per day late, adjust as needed
        }

        borrowing.setReturnDate(returnDate);
        borrowing.setFineAmount(fine);
        borrowing.setIsReturned(true);
        borrowingRepository.save(borrowing);

        // Increment available copies
        bookRepository.incrementAvailableCopies(borrowing.getBook().getId());

        return toResponse(borrowing);
    }

    @Transactional
    public void deleteBorrowing(Long id) {
        borrowingRepository.deleteById(id);
    }

    @Transactional
    public void processOverdueFines() {
        List<Borrowing> overdue = borrowingRepository.findByIsReturnedFalseAndDueDateBefore(LocalDate.now());
        for (Borrowing b : overdue) {
            long daysLate = ChronoUnit.DAYS.between(b.getDueDate(), LocalDate.now());
            double fine = daysLate * 5.0;
            b.setFineAmount(fine);
            borrowingRepository.save(b);
        }
    }

    private BorrowingResponse toResponse(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getStudent().getId(),
                borrowing.getStudent().getUser().getFullName(),
                borrowing.getBook().getId(),
                borrowing.getBook().getTitle(),
                borrowing.getBook().getIsbn(),
                borrowing.getIssueDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getFineAmount(),
                borrowing.getIsReturned(),
                borrowing.getRemarks(),
                borrowing.getCreatedAt()
        );
    }
}