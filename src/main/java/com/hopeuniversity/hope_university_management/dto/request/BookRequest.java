package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    private String author;

    private String publisher;

    private Integer publicationYear;

    private String category;

    private String location;

    @NotNull
    private Integer totalCopies;
}