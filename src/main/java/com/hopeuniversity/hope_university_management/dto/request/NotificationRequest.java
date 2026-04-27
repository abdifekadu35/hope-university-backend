package com.hopeuniversity.hope_university_management.dto.request;

import com.hopeuniversity.hope_university_management.domain.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotNull
    private Long userId;

    private String subject;

    @NotNull
    private String content;

    private NotificationType type = NotificationType.EMAIL;
}