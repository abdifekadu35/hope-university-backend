package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Notification;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.NotificationStatus;
import com.hopeuniversity.hope_university_management.domain.enums.NotificationType;
import com.hopeuniversity.hope_university_management.domain.repositories.NotificationRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import com.hopeuniversity.hope_university_management.dto.request.NotificationRequest;
import com.hopeuniversity.hope_university_management.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setType(request.getType());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);

        // Try to send immediately if email
        if (request.getType() == NotificationType.EMAIL) {
            sendNow(saved);
        }

        return toResponse(saved);
    }

    @Async
    public void sendNow(Notification notification) {
        boolean success = false;
        if (notification.getType() == NotificationType.EMAIL) {
            success = emailService.sendSimpleEmail(
                    notification.getUser().getEmail(),
                    notification.getSubject() != null ? notification.getSubject() : "Hope University Notification",
                    notification.getContent()
            );
        } else {
            // For SMS or other types, you would integrate with a provider.
            // For now, we just mark as SENT if type is IN_APP (no external action)
            success = true;
        }

        if (success) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(java.time.LocalDateTime.now());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage("Email delivery failed");
        }
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<NotificationResponse> getNotificationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUser(user, pageable).map(this::toResponse);
    }

    public Page<NotificationResponse> getNotificationsByEmail(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUser(user, pageable).map(this::toResponse);
    }

    public Page<NotificationResponse> getUnreadNotificationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndIsRead(user, false, pageable).map(this::toResponse);
    }

    public Page<NotificationResponse> getUnreadNotificationsByEmail(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndIsRead(user, false, pageable).map(this::toResponse);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public void resendFailedNotifications() {
        var failed = notificationRepository.findByStatus(NotificationStatus.FAILED);
        for (Notification n : failed) {
            sendNow(n);
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getUser().getEmail(),
                notification.getSubject(),
                notification.getContent(),
                notification.getType(),
                notification.getStatus(),
                notification.getErrorMessage(),
                notification.getIsRead(),
                notification.getReadAt(),
                notification.getSentAt(),
                notification.getCreatedAt()
        );
    }
}