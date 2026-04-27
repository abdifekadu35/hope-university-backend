package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Instructor;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.repositories.InstructorRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    // ---------------------- Student Excel ----------------------
    public ByteArrayInputStream generateStudentListExcel() throws IOException {
        List<Student> students = studentRepository.findAllActive();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            String[] columns = {
                    "ID", "Student ID", "First Name", "Father Name", "Last Name", "Full Name", "Email",
                    "Department", "Enrollment Year", "Phone", "National ID", "Place of Birth", "Gender",
                    "Date of Birth", "Current Address", "Country", "City", "Postal Code",
                    "Faculty", "Program", "Mode of Study", "Academic Status",
                    "Guardian Full Name", "Guardian Relationship", "Guardian Phone", "Guardian Email",
                    "Profile Picture URL", "Created At", "Updated At"
            };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getStudentId());
                row.createCell(2).setCellValue(s.getFirstName());
                row.createCell(3).setCellValue(s.getFatherName());
                row.createCell(4).setCellValue(s.getLastName());
                row.createCell(5).setCellValue(s.getFullName());
                row.createCell(6).setCellValue(s.getUser().getEmail());
                row.createCell(7).setCellValue(s.getDepartment() != null ? s.getDepartment().getName() : "");
                row.createCell(8).setCellValue(s.getEnrollmentYear() != null ? s.getEnrollmentYear() : 0);
                row.createCell(9).setCellValue(s.getPhone() != null ? s.getPhone() : "");
                row.createCell(10).setCellValue(s.getNationalId() != null ? s.getNationalId() : "");
                row.createCell(11).setCellValue(s.getPlaceOfBirth() != null ? s.getPlaceOfBirth() : "");
                row.createCell(12).setCellValue(s.getGender() != null ? s.getGender() : "");
                row.createCell(13).setCellValue(s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "");
                row.createCell(14).setCellValue(s.getCurrentAddress() != null ? s.getCurrentAddress() : "");
                row.createCell(15).setCellValue(s.getCountry() != null ? s.getCountry() : "");
                row.createCell(16).setCellValue(s.getCity() != null ? s.getCity() : "");
                row.createCell(17).setCellValue(s.getPostalCode() != null ? s.getPostalCode() : "");
                row.createCell(18).setCellValue(s.getFaculty() != null ? s.getFaculty() : "");
                row.createCell(19).setCellValue(s.getProgram() != null ? s.getProgram() : "");
                row.createCell(20).setCellValue(s.getModeOfStudy() != null ? s.getModeOfStudy() : "");
                row.createCell(21).setCellValue(s.getAcademicStatus() != null ? s.getAcademicStatus() : "");
                row.createCell(22).setCellValue(s.getGuardianFullName() != null ? s.getGuardianFullName() : "");
                row.createCell(23).setCellValue(s.getGuardianRelationship() != null ? s.getGuardianRelationship() : "");
                row.createCell(24).setCellValue(s.getGuardianPhone() != null ? s.getGuardianPhone() : "");
                row.createCell(25).setCellValue(s.getGuardianEmail() != null ? s.getGuardianEmail() : "");
                row.createCell(26).setCellValue(s.getProfilePictureUrl() != null ? s.getProfilePictureUrl() : "");
                row.createCell(27).setCellValue(s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");
                row.createCell(28).setCellValue(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ---------------------- Student PDF (iText7) ----------------------
    public ByteArrayInputStream generateStudentListPdf() throws IOException {
        List<Student> students = studentRepository.findAllActive();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Student List Report").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(" "));

        float[] columnWidths = {2, 3, 5, 4, 3, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"ID", "Student ID", "Full Name", "Email", "Department", "Phone"};
        for (String h : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(h).setBold().setFontSize(10)));
        }

        for (Student s : students) {
            String fullName = s.getFirstName() + " " + s.getFatherName() + " " + s.getLastName();
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(s.getId())).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(s.getStudentId()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(fullName).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(s.getUser().getEmail()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(s.getDepartment() != null ? s.getDepartment().getName() : "").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(s.getPhone() != null ? s.getPhone() : "").setFontSize(9)));
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ---------------------- Instructor Excel (POI only) ----------------------
    public byte[] generateInstructorsExcel() throws IOException {
        List<Instructor> instructors = instructorRepository.findAllActive();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Instructors");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Instructor ID", "Full Name", "Email", "Department", "Office", "Phone", "Hire Date", "Title"};
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (Instructor instructor : instructors) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(instructor.getId());
                row.createCell(1).setCellValue(instructor.getInstructorId());
                row.createCell(2).setCellValue(instructor.getUser().getFullName());
                row.createCell(3).setCellValue(instructor.getUser().getEmail());
                row.createCell(4).setCellValue(instructor.getDepartment() != null ? instructor.getDepartment().getName() : "");
                row.createCell(5).setCellValue(instructor.getOffice());
                row.createCell(6).setCellValue(instructor.getPhone());
                row.createCell(7).setCellValue(instructor.getHireDate() != null ? instructor.getHireDate().toString() : "");
                row.createCell(8).setCellValue(instructor.getTitle());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
        }
        return out.toByteArray();
    }

    // ---------------------- Instructor PDF (iText7) ----------------------
    public byte[] generateInstructorsPdf() throws IOException {
        List<Instructor> instructors = instructorRepository.findAllActive();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Instructors List").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(" "));

        float[] columnWidths = {1, 2, 3, 4, 2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"ID", "Instructor ID", "Full Name", "Email", "Department", "Office", "Phone", "Hire Date", "Title"};
        for (String h : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(h).setBold().setFontSize(10)));
        }

        for (Instructor instructor : instructors) {
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(instructor.getId())).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getInstructorId()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getUser().getFullName()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getUser().getEmail()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getDepartment() != null ? instructor.getDepartment().getName() : "").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getOffice()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getPhone()).setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getHireDate() != null ? instructor.getHireDate().toString() : "").setFontSize(9)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(instructor.getTitle()).setFontSize(9)));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}