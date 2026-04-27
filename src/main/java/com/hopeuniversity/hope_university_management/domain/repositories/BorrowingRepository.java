package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Book;
import com.hopeuniversity.hope_university_management.domain.entities.Borrowing;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    Page<Borrowing> findByStudent(Student student, Pageable pageable);

    Page<Borrowing> findByBook(Book book, Pageable pageable);

    Optional<Borrowing> findByStudentAndBookAndIsReturnedFalse(Student student, Book book);

    List<Borrowing> findByIsReturnedFalseAndDueDateBefore(LocalDate date);

    @Modifying
    @Query("UPDATE Borrowing b SET b.isReturned = true, b.returnDate = CURRENT_DATE, b.fineAmount = :fine WHERE b.id = :id")
    void markAsReturned(@Param("id") Long id, @Param("fine") Double fine);

    // Method used by StatisticsService
    long countByIsReturnedFalse();
}