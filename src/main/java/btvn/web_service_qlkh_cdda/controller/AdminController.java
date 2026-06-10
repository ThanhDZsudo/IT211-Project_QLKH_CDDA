package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CourseRepository courseRepository;

    @PostMapping("/courses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<Course>> createCourse(@RequestBody Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new RuntimeException("Mã khóa học đã tồn tại!");
        }
        Course savedCourse = courseRepository.save(course);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Tạo khóa học thành công", savedCourse, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<List<CourseDTO>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findAll(pageable);

        // Áp dụng Java Stream API đúng như yêu cầu UC-02
        List<CourseDTO> courseDTOs = coursePage.getContent().stream()
                .map(c -> CourseDTO.builder()
                        .id(c.getId())
                        .courseCode(c.getCourseCode())
                        .courseName(c.getCourseName())
                        .credit(c.getCredit())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiDataResonse<>(true, "Lấy danh sách khóa học thành công", courseDTOs, null, HttpStatus.OK));
    }
}