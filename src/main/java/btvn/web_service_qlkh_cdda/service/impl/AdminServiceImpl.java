package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.Role;
import btvn.web_service_qlkh_cdda.model.entity.Users;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.repository.RoleRepository;
import btvn.web_service_qlkh_cdda.repository.UserRepository;
import btvn.web_service_qlkh_cdda.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ===================== COURSE =====================

    @Override
    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new RuntimeException("Mã khóa học đã tồn tại!");
        }
        return courseRepository.save(course);
    }

    @Override
    public List<CourseDTO> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findAll(pageable);
        // FR-05 (UC-02): Bắt buộc dùng Java Stream API để map Entity -> DTO
        return coursePage.getContent().stream()
                .map(c -> CourseDTO.builder()
                        .id(c.getId())
                        .courseCode(c.getCourseCode())
                        .courseName(c.getCourseName())
                        .credit(c.getCredit())
                        .build())
                .collect(Collectors.toList());
    }

    // ===================== USER (FR-05) =====================

    @Override
    public List<Users> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> userPage = userRepository.findAll(pageable);
        // FR-05: Dùng Java Stream API để xử lý tập hợp dữ liệu
        return userPage.getContent().stream()
                .collect(Collectors.toList());
    }

    @Override
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với username: " + username));
    }

    @Override
    @Transactional
    public Users createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        // Lấy role từ userDTO, nếu không có thì mặc định là STUDENT
        String roleName = (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty())
                ? userDTO.getRoles().get(0).getRoleName()
                : "STUDENT";

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền: " + roleName));

        Users newUser = Users.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .enabled(true)
                .roles(List.of(role))
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public Users updateUser(Long userId, UserDTO userDTO) {
        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhone(userDTO.getPhone());

        // Cập nhật mật khẩu nếu có gửi lên
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        // Soft delete: Không xóa cứng, chỉ set enabled = false
        existingUser.setEnabled(false);
        userRepository.save(existingUser);
    }
}