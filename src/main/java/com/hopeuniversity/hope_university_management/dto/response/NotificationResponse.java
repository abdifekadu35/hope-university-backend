package com.hopeuniversity.hope_university_management.dto.response;

import com.hopeuniversity.hope_university_management.domain.enums.NotificationStatus;
import com.hopeuniversity.hope_university_management.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String subject;
    private String content;
    private NotificationType type;
    private NotificationStatus status;
    private String errorMessage;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}