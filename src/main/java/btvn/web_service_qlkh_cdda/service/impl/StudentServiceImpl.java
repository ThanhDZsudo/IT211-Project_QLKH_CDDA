package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.Enrollment;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import btvn.web_service_qlkh_cdda.model.entity.Users;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.repository.EnrollmentRepository;
import btvn.web_service_qlkh_cdda.repository.SubmissionRepository;
import btvn.web_service_qlkh_cdda.repository.UserRepository;
import btvn.web_service_qlkh_cdda.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    @Transactional
    public void enrollCourse(Long courseId, String username) {
        Users student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

        if (enrollmentRepository.existsByStudent_UserIdAndCourse_Id(student.getUserId(), courseId)) {
            throw new RuntimeException("Bạn đã đăng ký khóa học này rồi!");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .build();
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public Submission submitProject(Long enrollmentId, Map<String, String> payload) {
        // Đảm bảo Enrollment có tồn tại trước khi cho nộp bài
        enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Đăng ký khóa học không tồn tại hoặc không hợp lệ"));

        Submission submission = Submission.builder()
                .enrollment(Enrollment.builder().id(enrollmentId).build())
                .githubUrl(payload.get("githubUrl"))
                .status("SUBMITTED")
                .build();

        return submissionRepository.save(submission);
    }
}