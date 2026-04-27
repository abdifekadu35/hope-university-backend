package com.hopeuniversity.hope_university_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank
    private String roomNumber;

    private String building;

    private Integer capacity;

    private String type;
}