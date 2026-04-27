package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String building;
    private Integer capacity;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}