package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class StudentIdGenerator {

    private final StudentRepository studentRepository;

    public String generateStudentId() {
        int currentYear = Year.now().getValue();
        String yearPrefix = String.valueOf(currentYear);
        // Find the highest sequence number for this year
        String lastId = studentRepository.findTopByOrderByIdDesc()
                .map(student -> student.getStudentId())
                .orElse(null);
        int nextSeq = 1;
        if (lastId != null && lastId.startsWith("STU" + yearPrefix)) {
            String seqStr = lastId.substring(7); // after STU2024
            try {
                nextSeq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                nextSeq = 1;
            }
        }
        String sequence = String.format("%04d", nextSeq);
        return "STU" + yearPrefix + sequence;
    }
}