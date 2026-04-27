package com.hopeuniversity.hope_university_management.config;

import com.hopeuniversity.hope_university_management.domain.entities.Role;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.RoleName;
import com.hopeuniversity.hope_university_management.domain.repositories.RoleRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create all 10 roles if they don't exist
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription(getRoleDescription(roleName));
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        }

        // Create default System Administrator if not exists
        if (userRepository.findByEmail("system@hope.edu").isEmpty()) {
            Role systemAdminRole = roleRepository.findByName(RoleName.SYSTEM_ADMIN)
                    .orElseThrow(() -> new RuntimeException("SYSTEM_ADMIN role not found"));
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("system@hope.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(systemAdminRole);
            admin.setActive(true);
            userRepository.save(admin);
            log.info("Created System Administrator - email: system@hope.edu, password: admin123");
        }
    }

    private String getRoleDescription(RoleName roleName) {
        return switch (roleName) {
            case SYSTEM_ADMIN -> "Full system control, manage all users and settings";
            case PRINCIPAL -> "Manage teachers, students, staff, approve admissions, view reports";
            case TEACHER -> "Manage classes, attendance, grades, assignments";
            case STUDENT -> "View timetable, grades, submit assignments";
            case PARENT -> "View child's progress, attendance, receive notifications";
            case ACCOUNTANT -> "Manage fees, invoices, payments, financial reports";
            case REGISTRAR -> "Student registration, admissions, record management";
            case LIBRARIAN -> "Manage books, issue/return, track fines";
            case IT_SUPPORT -> "System maintenance, user support, logs";
            case EXAM_OFFICER -> "Exam schedules, grading, result publication";
        };
    }
}