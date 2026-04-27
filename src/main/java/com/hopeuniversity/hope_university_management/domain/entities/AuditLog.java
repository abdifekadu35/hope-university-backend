package com.hopeuniversity.hope_university_management.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // who performed the action

    private String action; // e.g., "CREATE", "UPDATE", "DELETE", "LOGIN"

    private String entityType; // e.g., "User", "Student", "Course"

    private Long entityId;

    private String oldValue; // JSON or simple string representation

    private String newValue;

    private String ipAddress;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}