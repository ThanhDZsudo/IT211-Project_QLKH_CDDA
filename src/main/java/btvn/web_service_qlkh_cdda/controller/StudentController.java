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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    // ĐÃ SỬA: Không gọi trực tiếp các Repository nữa, chỉ gọi thông qua Service chuẩn mô hình phân lớp
    private final StudentService studentService;

    // FR-06: Sinh viên Đăng ký tham gia khóa học
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiDataResonse<?>> enrollCourse(@PathVariable Long courseId, Authentication authentication) {
        String username = authentication.getName();
        studentService.enrollCourse(courseId, username);

        return new ResponseEntity<>(new ApiDataResonse<>(true, "Đăng ký khóa học thành công", null, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    // FR-07: Sinh viên Nộp bài tập / Đồ án (Gửi link GitHub)
    @PostMapping("/submissions/{enrollmentId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ApiDataResonse<Submission>> submitProject(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, String> payload) {

        Submission saved = studentService.submitProject(enrollmentId, payload);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Nộp đồ án thành công", saved, null, HttpStatus.OK));
    }
}