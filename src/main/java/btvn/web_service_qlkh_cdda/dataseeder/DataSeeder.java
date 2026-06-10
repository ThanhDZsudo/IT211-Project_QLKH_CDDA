package btvn.web_service_qlkh_cdda.dataseeder; // ĐÃ CHUYỂN SANG PACKAGE DATASEEDER

import btvn.web_service_qlkh_cdda.model.entity.*;
import btvn.web_service_qlkh_cdda.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. Tạo 3 Quyền cốt lõi (Theo đúng logic hệ thống chỉ có 3 quyền này)
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().roleName("ADMIN").build());
            roleRepository.save(Role.builder().roleName("LECTURER").build());
            roleRepository.save(Role.builder().roleName("STUDENT").build());
        }

        // 2. Tạo 5 Users (1 Admin, 1 Giảng viên, 3 Sinh viên)
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").get();
            Role lecRole = roleRepository.findByRoleName("LECTURER").get();
            Role stRole = roleRepository.findByRoleName("STUDENT").get();

            // Mật khẩu chung cho tất cả là: 123
            userRepository.save(Users.builder().username("admin").password(passwordEncoder.encode("123")).fullName("Quản trị viên").email("admin@ptit.edu.vn").phone("0111111111").enabled(true).roles(List.of(adminRole)).build());
            userRepository.save(Users.builder().username("giangvien").password(passwordEncoder.encode("123")).fullName("Giảng viên A").email("gv@ptit.edu.vn").phone("0222222222").enabled(true).roles(List.of(lecRole)).build());
            userRepository.save(Users.builder().username("sv1").password(passwordEncoder.encode("123")).fullName("Sinh viên 1").email("sv1@ptit.edu.vn").phone("0333333331").enabled(true).roles(List.of(stRole)).build());
            userRepository.save(Users.builder().username("sv2").password(passwordEncoder.encode("123")).fullName("Sinh viên 2").email("sv2@ptit.edu.vn").phone("0333333332").enabled(true).roles(List.of(stRole)).build());
            userRepository.save(Users.builder().username("sv3").password(passwordEncoder.encode("123")).fullName("Sinh viên 3").email("sv3@ptit.edu.vn").phone("0333333333").enabled(true).roles(List.of(stRole)).build());
        }

        // 3. Tạo 5 Khóa học
        if (courseRepository.count() == 0) {
            courseRepository.save(Course.builder().courseCode("INT1").courseName("Lập trình Java Web").credit(3).build());
            courseRepository.save(Course.builder().courseCode("INT2").courseName("Cơ sở dữ liệu MySQL").credit(3).build());
            courseRepository.save(Course.builder().courseCode("INT3").courseName("Kiến trúc phần mềm").credit(3).build());
            courseRepository.save(Course.builder().courseCode("INT4").courseName("Hệ điều hành").credit(2).build());
            courseRepository.save(Course.builder().courseCode("INT5").courseName("Phân tích thiết kế hệ thống").credit(3).build());
        }

        // 4. Tạo 5 Đăng ký khóa học (Enrollment)
        if (enrollmentRepository.count() == 0) {
            Users sv1 = userRepository.findByUsername("sv1").get();
            Users sv2 = userRepository.findByUsername("sv2").get();
            Users sv3 = userRepository.findByUsername("sv3").get();

            List<Course> courses = courseRepository.findAll();

            enrollmentRepository.save(Enrollment.builder().student(sv1).course(courses.get(0)).enrollDate(LocalDateTime.now()).build());
            enrollmentRepository.save(Enrollment.builder().student(sv1).course(courses.get(1)).enrollDate(LocalDateTime.now()).build());
            enrollmentRepository.save(Enrollment.builder().student(sv2).course(courses.get(0)).enrollDate(LocalDateTime.now()).build());
            enrollmentRepository.save(Enrollment.builder().student(sv2).course(courses.get(2)).enrollDate(LocalDateTime.now()).build());
            enrollmentRepository.save(Enrollment.builder().student(sv3).course(courses.get(3)).enrollDate(LocalDateTime.now()).build());
        }

        // 5. Tạo 5 Bài nộp đồ án (Submission)
        if (submissionRepository.count() == 0) {
            List<Enrollment> enrollments = enrollmentRepository.findAll();

            submissionRepository.save(Submission.builder().enrollment(enrollments.get(0)).githubUrl("https://github.com/sv1/java-web").status("SUBMITTED").build());
            submissionRepository.save(Submission.builder().enrollment(enrollments.get(1)).githubUrl("https://github.com/sv1/mysql").status("GRADED").score(9.5).feedback("Database thiết kế rất tốt").build());
            submissionRepository.save(Submission.builder().enrollment(enrollments.get(2)).githubUrl("https://github.com/sv2/java-web").status("LATE").build());
            submissionRepository.save(Submission.builder().enrollment(enrollments.get(3)).githubUrl("https://github.com/sv2/architecture").status("SUBMITTED").build());
            submissionRepository.save(Submission.builder().enrollment(enrollments.get(4)).githubUrl("https://github.com/sv3/os").status("GRADED").score(8.0).feedback("Cần cải thiện luồng thread").build());
        }

    }
}