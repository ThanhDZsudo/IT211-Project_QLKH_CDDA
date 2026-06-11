package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService; // Gọi qua Service chuẩn kiến trúc

    @PostMapping("/courses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<Course>> createCourse(@Valid @RequestBody Course course) {
        Course savedCourse = adminService.createCourse(course);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Tạo khóa học thành công", savedCourse, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<List<CourseDTO>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CourseDTO> courseDTOs = adminService.getAllCourses(page, size);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Lấy danh sách khóa học thành công", courseDTOs, null, HttpStatus.OK));
    }
}