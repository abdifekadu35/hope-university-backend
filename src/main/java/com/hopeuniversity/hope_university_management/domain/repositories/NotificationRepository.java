package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Notification;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndIsRead(User user, Boolean isRead, Pageable pageable);

    List<Notification> findByStatus(NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
}