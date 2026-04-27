package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Department;
import com.hopeuniversity.hope_university_management.domain.entities.Role;
import com.hopeuniversity.hope_university_management.domain.entities.Student;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.RoleName;
import com.hopeuniversity.hope_university_management.domain.repositories.DepartmentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.RoleRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.StudentRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import com.hopeuniversity.hope_university_management.dto.request.StudentRequest;
import com.hopeuniversity.hope_university_management.dto.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.layout.properties.BorderRadius;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;


@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentIdGenerator studentIdGenerator;
    // private final NotificationService notificationService;

    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAllActive(pageable).map(this::toResponse);
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return toResponse(student);
    }

    public StudentResponse getStudentByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return toResponse(student);
    }

    public StudentResponse getStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return toResponse(student);
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        // 1. Generate student ID
        String generatedStudentId = studentIdGenerator.generateStudentId();

        // 2. Create User account with default password
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new RuntimeException("STUDENT role not found"));

        String fullName = request.getFirstName() + " " + request.getFatherName() + " " + request.getLastName();
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(request.getEmail());
        String defaultPassword = "12345678";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setRole(studentRole);
        user.setActive(true);
        user.setMustChangePassword(true);
        user = userRepository.save(user);

        // 3. Create Student profile
        Student student = new Student();
        student.setStudentId(generatedStudentId);
        student.setFirstName(request.getFirstName());
        student.setFatherName(request.getFatherName());
        student.setLastName(request.getLastName());
        student.setUser(user);
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(dept);
        }
        student.setEnrollmentYear(request.getEnrollmentYear());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setProfilePictureUrl(request.getProfilePictureUrl());
        student.setNationalId(request.getNationalId());
        student.setPlaceOfBirth(request.getPlaceOfBirth());
        student.setGender(request.getGender());
        student.setCurrentAddress(request.getCurrentAddress());
        student.setCountry(request.getCountry());
        student.setCity(request.getCity());
        student.setPostalCode(request.getPostalCode());
        student.setFaculty(request.getFaculty());
        student.setProgram(request.getProgram());
        student.setModeOfStudy(request.getModeOfStudy());
        student.setAcademicStatus(request.getAcademicStatus());
        student.setGuardianFullName(request.getGuardianFullName());
        student.setGuardianRelationship(request.getGuardianRelationship());
        student.setGuardianPhone(request.getGuardianPhone());
        student.setGuardianEmail(request.getGuardianEmail());

        Student saved = studentRepository.save(student);

        // 4. Send email (optional)
        // notificationService.sendEmail(user.getEmail(), "Account Created", "...");

        return toResponse(saved);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFirstName(request.getFirstName());
        student.setFatherName(request.getFatherName());
        student.setLastName(request.getLastName());
        student.setEnrollmentYear(request.getEnrollmentYear());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setProfilePictureUrl(request.getProfilePictureUrl());
        student.setNationalId(request.getNationalId());
        student.setPlaceOfBirth(request.getPlaceOfBirth());
        student.setGender(request.getGender());
        student.setCurrentAddress(request.getCurrentAddress());
        student.setCountry(request.getCountry());
        student.setCity(request.getCity());
        student.setPostalCode(request.getPostalCode());
        student.setFaculty(request.getFaculty());
        student.setProgram(request.getProgram());
        student.setModeOfStudy(request.getModeOfStudy());
        student.setAcademicStatus(request.getAcademicStatus());
        student.setGuardianFullName(request.getGuardianFullName());
        student.setGuardianRelationship(request.getGuardianRelationship());
        student.setGuardianPhone(request.getGuardianPhone());
        student.setGuardianEmail(request.getGuardianEmail());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(dept);
        }

        // Update linked user
        User user = student.getUser();
        String fullName = request.getFirstName() + " " + request.getFatherName() + " " + request.getLastName();
        if (!user.getFullName().equals(fullName)) {
            user.setFullName(fullName);
        }
        if (!user.getEmail().equals(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);

        Student updated = studentRepository.save(student);
        return toResponse(updated);
    }
    // ---------- Bulk Import (CSV & Excel) ----------
    public Map<String, Object> bulkImport(MultipartFile file) throws IOException {
        List<StudentRequest> requests = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new RuntimeException("Invalid file name");

        // Parse CSV or Excel
        if (fileName.endsWith(".csv")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line = reader.readLine(); // header
                if (line == null) throw new RuntimeException("Empty CSV file");
                int rowNum = 1;
                while ((line = reader.readLine()) != null) {
                    rowNum++;
                    if (line.trim().isEmpty()) continue;
                    String[] cols = line.split(",");
                    if (cols.length < 22) {
                        errors.add("Row " + rowNum + ": only " + cols.length + " columns (expected 22)");
                        continue;
                    }
                    try {
                        requests.add(parseStudentRequestFromCsv(cols));
                    } catch (Exception e) {
                        errors.add("Row " + rowNum + ": " + e.getMessage());
                    }
                }
            }
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            int rowNum = 0;
            for (Row row : sheet) {
                rowNum++;
                if (row.getRowNum() == 0) continue;
                try {
                    requests.add(parseStudentRequestFromExcel(row, formatter));
                } catch (Exception e) {
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }
            workbook.close();
        } else {
            throw new RuntimeException("Unsupported file type. Please upload CSV or Excel file.");
        }

        int created = 0;
        for (StudentRequest req : requests) {
            try {
                createStudent(req);
                created++;
            } catch (Exception e) {
                errors.add(req.getEmail() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("created", created);
        result.put("errors", errors);
        return result;
    }

    private StudentRequest parseStudentRequestFromCsv(String[] cols) {
        StudentRequest req = new StudentRequest();
        req.setFirstName(cols[0]);
        req.setFatherName(cols[1]);
        req.setLastName(cols[2]);
        req.setEmail(cols[3]);
        req.setDepartmentId(parseLong(cols[4]));
        req.setEnrollmentYear(parseInteger(cols[5]));
        req.setPhone(cols[6]);
        req.setPlaceOfBirth(cols[7]);
        req.setGender(cols[8]);
        req.setDateOfBirth(parseLocalDate(cols[9]));
        req.setCurrentAddress(cols[10]);
        req.setCountry(cols[11]);
        req.setCity(cols[12]);
        req.setPostalCode(cols[13]);
        req.setFaculty(cols[14]);
        req.setProgram(cols[15]);
        req.setModeOfStudy(cols[16]);
        req.setAcademicStatus(cols[17]);
        req.setGuardianFullName(cols[18]);
        req.setGuardianRelationship(cols[19]);
        req.setGuardianPhone(cols[20]);
        req.setGuardianEmail(cols[21]);
        return req;
    }

    private StudentRequest parseStudentRequestFromExcel(Row row, DataFormatter formatter) {
        StudentRequest req = new StudentRequest();
        req.setFirstName(formatter.formatCellValue(row.getCell(0)));
        req.setFatherName(formatter.formatCellValue(row.getCell(1)));
        req.setLastName(formatter.formatCellValue(row.getCell(2)));
        req.setEmail(formatter.formatCellValue(row.getCell(3)));
        req.setDepartmentId(parseLong(formatter.formatCellValue(row.getCell(4))));
        req.setEnrollmentYear(parseInteger(formatter.formatCellValue(row.getCell(5))));
        req.setPhone(formatter.formatCellValue(row.getCell(6)));
        req.setPlaceOfBirth(formatter.formatCellValue(row.getCell(7)));
        req.setGender(formatter.formatCellValue(row.getCell(8)));
        req.setDateOfBirth(parseLocalDate(formatter.formatCellValue(row.getCell(9))));
        req.setCurrentAddress(formatter.formatCellValue(row.getCell(10)));
        req.setCountry(formatter.formatCellValue(row.getCell(11)));
        req.setCity(formatter.formatCellValue(row.getCell(12)));
        req.setPostalCode(formatter.formatCellValue(row.getCell(13)));
        req.setFaculty(formatter.formatCellValue(row.getCell(14)));
        req.setProgram(formatter.formatCellValue(row.getCell(15)));
        req.setModeOfStudy(formatter.formatCellValue(row.getCell(16)));
        req.setAcademicStatus(formatter.formatCellValue(row.getCell(17)));
        req.setGuardianFullName(formatter.formatCellValue(row.getCell(18)));
        req.setGuardianRelationship(formatter.formatCellValue(row.getCell(19)));
        req.setGuardianPhone(formatter.formatCellValue(row.getCell(20)));
        req.setGuardianEmail(formatter.formatCellValue(row.getCell(21)));
        return req;
    }

    private Long parseLong(String value) {
        try { return Long.parseLong(value); } catch (Exception e) { return null; }
    }
    private Integer parseInteger(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return null; }
    }
    private LocalDateTime parseLocalDate(String value) {
        try { return LocalDate.parse(value).atStartOfDay(); } catch (Exception e) { return null; }
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.softDeleteById(id);
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentId(),
                student.getUser().getId(),
                student.getFirstName(),
                student.getFatherName(),
                student.getLastName(),
                student.getFullName(),
                student.getUser().getEmail(),
                student.getDepartment() != null ? student.getDepartment().getId() : null,
                student.getDepartment() != null ? student.getDepartment().getName() : null,
                student.getEnrollmentYear(),
                student.getPhone(),
                student.getAddress(),
                student.getDateOfBirth(),
                student.getProfilePictureUrl(),
                student.getNationalId(),
                student.getPlaceOfBirth(),
                student.getGender(),
                student.getCurrentAddress(),
                student.getCountry(),
                student.getCity(),
                student.getPostalCode(),
                student.getFaculty(),
                student.getProgram(),
                student.getModeOfStudy(),
                student.getAcademicStatus(),
                student.getGuardianFullName(),
                student.getGuardianRelationship(),
                student.getGuardianPhone(),
                student.getGuardianEmail(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }





    public ByteArrayInputStream generateIdCard(Long studentId) throws IOException {
        Student student = studentRepository.findActiveById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Credit‑card size: 85.6mm x 54mm (landscape)
        float mmToPt = 2.83465f;
        float widthMm = 85.6f;
        float heightMm = 54f;
        PageSize cardSize = new PageSize(widthMm * mmToPt, heightMm * mmToPt);
        pdfDoc.setDefaultPageSize(cardSize);

        Document document = new Document(pdfDoc);
        document.setMargins(5, 5, 5, 5);

        // Two‑column table: left = front, right = back
        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        mainTable.setWidth(UnitValue.createPercentValue(100));
        mainTable.setMarginTop(5);

        Cell frontCell = new Cell();
        frontCell.setBorder(Border.NO_BORDER);
        createFrontContent(frontCell, student, pdfDoc);
        mainTable.addCell(frontCell);

        Cell backCell = new Cell();
        backCell.setBorder(Border.NO_BORDER);
        createBackContent(backCell, student, pdfDoc);
        mainTable.addCell(backCell);

        document.add(mainTable);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void createFrontContent(Cell cell, Student student, PdfDocument pdfDoc) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont normalFont = PdfFontFactory.createFont("Helvetica");
        PdfFont smallFont = PdfFontFactory.createFont("Helvetica");

        // Flag bar (half width)
        float pageWidth = pdfDoc.getDefaultPageSize().getWidth() / 2;
        Table flagBar = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        flagBar.setWidth(pageWidth);
        flagBar.setMarginBottom(4);
        Cell green = new Cell().setBackgroundColor(new DeviceRgb(7, 137, 48)).setHeight(4);
        green.setBorder(Border.NO_BORDER);
        Cell yellow = new Cell().setBackgroundColor(new DeviceRgb(252, 221, 9)).setHeight(4);
        yellow.setBorder(Border.NO_BORDER);
        Cell red = new Cell().setBackgroundColor(new DeviceRgb(218, 18, 26)).setHeight(4);
        red.setBorder(Border.NO_BORDER);
        flagBar.addCell(green);
        flagBar.addCell(yellow);
        flagBar.addCell(red);
        cell.add(flagBar);

        // Header: logo + title + small ID
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMarginBottom(5);

        Cell logoCell = new Cell();
        logoCell.setBorder(Border.NO_BORDER);
        Paragraph logo = new Paragraph("ID")
                .setFont(boldFont).setFontSize(12).setBackgroundColor(new DeviceRgb(37, 99, 235))
                .setTextAlignment(TextAlignment.CENTER);
        logoCell.add(logo);
        headerTable.addCell(logoCell);

        Cell titleCell = new Cell();
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.add(new Paragraph("FEDERAL DIGITAL ID").setFont(boldFont).setFontSize(7));
        titleCell.add(new Paragraph("National Identity Registry").setFont(normalFont).setFontSize(5).setFontColor(ColorConstants.GRAY));
        headerTable.addCell(titleCell);

        Cell idCell = new Cell();
        idCell.setBorder(Border.NO_BORDER);
        idCell.add(new Paragraph(student.getStudentId()).setFont(smallFont).setFontSize(5));
        headerTable.addCell(idCell);

        cell.add(headerTable);

        // Front content: photo + grid
        Table contentTable = new Table(UnitValue.createPercentArray(new float[]{1, 1.5f}));
        contentTable.setWidth(UnitValue.createPercentValue(100));
        contentTable.setMarginBottom(5);

        Cell photoCell = new Cell();
        photoCell.setBorder(Border.NO_BORDER);
        if (student.getProfilePictureUrl() != null && student.getProfilePictureUrl().contains(",")) {
            try {
                String base64 = student.getProfilePictureUrl().split(",")[1];
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64);
                Image photo = new Image(ImageDataFactory.create(imageBytes));
                photo.scaleToFit(45, 45);
                photoCell.add(photo);
            } catch (Exception e) {
                photoCell.add(new Paragraph("No photo").setFont(normalFont).setFontSize(6));
            }
        } else {
            photoCell.add(new Paragraph("No photo").setFont(normalFont).setFontSize(6));
        }
        contentTable.addCell(photoCell);

        Cell gridCell = new Cell();
        gridCell.setBorder(Border.NO_BORDER);
        Table grid = new Table(UnitValue.createPercentArray(new float[]{1, 1.5f}));
        grid.setWidth(UnitValue.createPercentValue(100));

        addFieldRow(grid, "FULL NAME", student.getFirstName() + " " + student.getFatherName() + " " + student.getLastName(), boldFont, normalFont);
        addFieldRow(grid, "SEX", student.getGender() != null ? student.getGender() : "-", boldFont, normalFont);
        addFieldRow(grid, "DATE OF BIRTH", student.getDateOfBirth() != null ? student.getDateOfBirth().toLocalDate().toString() : "-", boldFont, normalFont);
        addFieldRow(grid, "NATIONALITY", "ETHIOPIAN", boldFont, normalFont);
        addFieldRow(grid, "STUDENT ID", student.getStudentId(), boldFont, normalFont);
        addFieldRow(grid, "ISSUE DATE", LocalDate.now().toString(), boldFont, normalFont);

        gridCell.add(grid);
        contentTable.addCell(gridCell);
        cell.add(contentTable);

        // Footer
        Paragraph footer = new Paragraph("OFFICIAL GOVERNMENT IDENTIFICATION")
                .setFont(smallFont).setFontSize(5).setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(229, 231, 235))
                .setBorderRadius(new BorderRadius(6)).setPadding(4);
        cell.add(footer);
    }

    private void createBackContent(Cell cell, Student student, PdfDocument pdfDoc) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont normalFont = PdfFontFactory.createFont("Helvetica");
        PdfFont smallFont = PdfFontFactory.createFont("Helvetica");

        // Flag bar (half width)
        float pageWidth = pdfDoc.getDefaultPageSize().getWidth() / 2;
        Table flagBar = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        flagBar.setWidth(pageWidth);
        flagBar.setMarginBottom(4);
        Cell green = new Cell().setBackgroundColor(new DeviceRgb(7, 137, 48)).setHeight(4);
        green.setBorder(Border.NO_BORDER);
        Cell yellow = new Cell().setBackgroundColor(new DeviceRgb(252, 221, 9)).setHeight(4);
        yellow.setBorder(Border.NO_BORDER);
        Cell red = new Cell().setBackgroundColor(new DeviceRgb(218, 18, 26)).setHeight(4);
        red.setBorder(Border.NO_BORDER);
        flagBar.addCell(green);
        flagBar.addCell(yellow);
        flagBar.addCell(red);
        cell.add(flagBar);

        // Back content: QR + details
        Table backTable = new Table(UnitValue.createPercentArray(new float[]{1, 1.5f}));
        backTable.setWidth(UnitValue.createPercentValue(100));
        backTable.setMarginTop(10);

        Cell qrCell = new Cell();
        qrCell.setBorder(Border.NO_BORDER);
        BarcodeQRCode qrCode = new BarcodeQRCode(student.getStudentId());
        com.itextpdf.layout.element.Image qrImage = new com.itextpdf.layout.element.Image(qrCode.createFormXObject(pdfDoc));
        qrImage.scaleToFit(55, 55);
        qrCell.add(qrImage);
        qrCell.add(new Paragraph("SCAN TO VERIFY").setFont(smallFont).setFontSize(5).setTextAlignment(TextAlignment.CENTER));
        backTable.addCell(qrCell);

        Cell detailsCell = new Cell();
        detailsCell.setBorder(Border.NO_BORDER);

        detailsCell.add(new Paragraph("RESIDENCY").setFont(boldFont).setFontSize(6).setFontColor(new DeviceRgb(37, 99, 235)));
        detailsCell.add(new Paragraph("Address: " + (student.getCurrentAddress() != null ? student.getCurrentAddress() : "-"))
                .setFont(normalFont).setFontSize(5).setMarginBottom(4));

        detailsCell.add(new Paragraph("CONTACT").setFont(boldFont).setFontSize(6).setFontColor(new DeviceRgb(37, 99, 235)));
        detailsCell.add(new Paragraph("Phone: " + (student.getPhone() != null ? student.getPhone() : "-"))
                .setFont(normalFont).setFontSize(5));
        detailsCell.add(new Paragraph("Email: " + student.getUser().getEmail())
                .setFont(normalFont).setFontSize(5).setMarginBottom(4));

        detailsCell.add(new Paragraph("EMERGENCY").setFont(boldFont).setFontSize(6).setFontColor(new DeviceRgb(37, 99, 235)));
        detailsCell.add(new Paragraph("Name: " + (student.getGuardianFullName() != null ? student.getGuardianFullName() : "-"))
                .setFont(normalFont).setFontSize(5));
        detailsCell.add(new Paragraph("Relation: " + (student.getGuardianRelationship() != null ? student.getGuardianRelationship() : "-"))
                .setFont(normalFont).setFontSize(5));
        detailsCell.add(new Paragraph("Phone: " + (student.getGuardianPhone() != null ? student.getGuardianPhone() : "-"))
                .setFont(normalFont).setFontSize(5).setMarginBottom(4));

        backTable.addCell(detailsCell);
        cell.add(backTable);

        // Footer
        Paragraph footer = new Paragraph("SECURE • VERIFIED • TRUSTED")
                .setFont(smallFont).setFontSize(5).setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(229, 231, 235))
                .setBorderRadius(new BorderRadius(6)).setPadding(4)
                .setMarginTop(8);
        cell.add(footer);
    }

    private void addFieldRow(Table table, String label, String value, PdfFont boldFont, PdfFont normalFont) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setFont(boldFont).setFontSize(5).setFontColor(ColorConstants.GRAY));
        labelCell.setBorder(Border.NO_BORDER);
        table.addCell(labelCell);

        Cell valueCell = new Cell();
        valueCell.add(new Paragraph(value).setFont(normalFont).setFontSize(6));
        valueCell.setBorder(Border.NO_BORDER);
        table.addCell(valueCell);
    }


}