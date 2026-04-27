package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Student exports (unchanged)
    @GetMapping("/students/excel")
    public ResponseEntity<Resource> exportStudentsExcel() {
        try {
            ByteArrayInputStream in = reportService.generateStudentListExcel();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/students/pdf")
    public ResponseEntity<Resource> exportStudentsPDF() {
        try {
            ByteArrayInputStream in = reportService.generateStudentListPdf();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.pdf")
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Instructor exports – corrected (no DocumentException)
    @GetMapping("/instructors/excel")
    public ResponseEntity<byte[]> exportInstructorsToExcel() {
        try {
            byte[] excelData = reportService.generateInstructorsExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=instructors.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/instructors/pdf")
    public ResponseEntity<byte[]> exportInstructorsToPdf() {
        try {
            byte[] pdfData = reportService.generateInstructorsPdf();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=instructors.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfData);
        } catch (IOException e) {   // <-- removed DocumentException, only IOException
            return ResponseEntity.internalServerError().build();
        }
    }
}