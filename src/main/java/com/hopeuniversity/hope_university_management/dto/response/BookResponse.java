package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String category;
    private String location;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}