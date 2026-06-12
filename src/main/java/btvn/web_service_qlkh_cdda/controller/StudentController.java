package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import btvn.web_service_qlkh_cdda.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // FR-06: Sinh viên Đăng ký tham gia khóa học
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiDataResonse<?>> enrollCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        studentService.enrollCourse(courseId, authentication.getName());
        return new ResponseEntity<>(
                new ApiDataResonse<>(true, "Đăng ký khóa học thành công", null, null, HttpStatus.CREATED),
                HttpStatus.CREATED);
    }

    // FR-07 (phần 1): Sinh viên Nộp link GitHub
    @PostMapping("/submissions/{enrollmentId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiDataResonse<Submission>> submitProject(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, String> payload) {
        Submission saved = studentService.submitProject(enrollmentId, payload);
        return ResponseEntity.ok(
                new ApiDataResonse<>(true, "Nộp đồ án thành công", saved, null, HttpStatus.OK));
    }

    /**
     * UC-05: Sinh viên nộp báo cáo dạng file (PDF/Word) lên Cloudinary.
     * Endpoint nhận multipart/form-data, upload lên đám mây và lưu URL vào DB.
     *
     * POST /api/v1/student/submissions/{enrollmentId}/upload
     * Content-Type: multipart/form-data
     * Body: file (MultipartFile)
     */
    @PostMapping("/submissions/{enrollmentId}/upload")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiDataResonse<Submission>> uploadReport(
            @PathVariable Long enrollmentId,
            @RequestParam("file") MultipartFile file) {
        Submission saved = studentService.uploadReport(enrollmentId, file);
        return ResponseEntity.ok(
                new ApiDataResonse<>(true, "Nộp báo cáo lên đám mây thành công", saved, null, HttpStatus.OK));
    }
}