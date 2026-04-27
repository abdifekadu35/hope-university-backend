package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Book;
import com.hopeuniversity.hope_university_management.domain.repositories.BookRepository;
import com.hopeuniversity.hope_university_management.dto.request.BookRequest;
import com.hopeuniversity.hope_university_management.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAllActive(pageable).map(this::toResponse);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return toResponse(book);
    }

    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN already exists");
        }
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setCategory(request.getCategory());
        book.setLocation(request.getLocation());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        // If ISBN changed, check uniqueness
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN already exists");
        }
        int oldTotal = book.getTotalCopies();
        int newTotal = request.getTotalCopies();
        int delta = newTotal - oldTotal;
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setCategory(request.getCategory());
        book.setLocation(request.getLocation());
        book.setTotalCopies(newTotal);
        book.setAvailableCopies(book.getAvailableCopies() + delta);
        Book updated = bookRepository.save(book);
        return toResponse(updated);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setDeletedAt(LocalDateTime.now());
            bookRepository.save(book);
        });
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getCategory(),
                book.getLocation(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}