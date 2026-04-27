package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.deletedAt IS NULL")
    Page<Book> findAllActive(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Book> findActiveById(@Param("id") Long id);

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies - 1 WHERE b.id = :id AND b.availableCopies > 0")
    int decrementAvailableCopies(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies + 1 WHERE b.id = :id")
    int incrementAvailableCopies(@Param("id") Long id);
}