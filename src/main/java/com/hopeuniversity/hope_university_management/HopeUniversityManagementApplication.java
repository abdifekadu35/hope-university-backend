package com.hopeuniversity.hope_university_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableAsync
public class HopeUniversityManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HopeUniversityManagementApplication.class, args);
	}
}