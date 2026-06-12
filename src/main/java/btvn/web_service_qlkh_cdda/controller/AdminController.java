package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.Users;
import btvn.web_service_qlkh_cdda.service.AdminService;
import btvn.web_service_qlkh_cdda.service.UserService;
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

    private final AdminService adminService;
    private final UserService userService;

    // ===================== COURSE MANAGEMENT =====================

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

    // ===================== USER MANAGEMENT (FR-05) =====================

    /**
     * FR-05: Lấy danh sách tất cả người dùng (có phân trang)
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<List<Users>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Users> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Lấy danh sách người dùng thành công", users, null, HttpStatus.OK));
    }

    /**
     * FR-05: Tìm kiếm người dùng theo username
     */
    @GetMapping("/users/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<Users>> getUserByUsername(@RequestParam String username) {
        Users user = adminService.getUserByUsername(username);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Tìm kiếm người dùng thành công", user, null, HttpStatus.OK));
    }

    /**
     * FR-05: Admin tạo tài khoản mới (có thể chỉ định role: STUDENT/LECTURER/ADMIN)
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<Users>> createUser(@Valid @RequestBody UserDTO userDTO) {
        Users savedUser = adminService.createUser(userDTO);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Tạo tài khoản người dùng thành công", savedUser, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    /**
     * FR-05: Cập nhật thông tin người dùng
     */
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<Users>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDTO userDTO) {
        Users updatedUser = adminService.updateUser(userId, userDTO);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Cập nhật người dùng thành công", updatedUser, null, HttpStatus.OK));
    }

    /**
     * FR-05: Vô hiệu hóa tài khoản người dùng (Deactivate - không xóa cứng)
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiDataResonse<?>> deactivateUser(@PathVariable Long userId) {
        adminService.deactivateUser(userId);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Vô hiệu hóa tài khoản thành công", null, null, HttpStatus.OK));
    }
}