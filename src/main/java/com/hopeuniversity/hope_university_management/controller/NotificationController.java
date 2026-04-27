package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.NotificationRequest;
import com.hopeuniversity.hope_university_management.dto.response.NotificationResponse;
import com.hopeuniversity.hope_university_management.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getAllNotifications(pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @PageableDefault(size = 10) Pageable pageable) {
        // Find user by email, then get notifications
        // We'll need a service method to get by email; add helper method in NotificationService
        // For brevity, assume we have user ID from principal; but we need email->user mapping.
        // Let's add a method in NotificationService: getNotificationsByEmail(String email)
        // I'll include it below.
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(userDetails.getUsername(), pageable));
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationResponse>> getMyUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails,
                                                                               @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByEmail(userDetails.getUsername(), pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PRINCIPAL', 'TEACHER')") // Teachers can send notifications to their students? Adjust as needed.
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        return new ResponseEntity<>(notificationService.createNotification(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-failed")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Void> resendFailed() {
        notificationService.resendFailedNotifications();
        return ResponseEntity.accepted().build();
    }
}