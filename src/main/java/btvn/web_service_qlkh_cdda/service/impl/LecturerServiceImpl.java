package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.LectureMaterial;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.repository.LectureMaterialRepository;
import btvn.web_service_qlkh_cdda.repository.SubmissionRepository;
import btvn.web_service_qlkh_cdda.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final LectureMaterialRepository lectureMaterialRepository;

    @Override
    @Transactional
    public Submission gradeSubmission(Long submissionId, Map<String, Object> gradeData) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp đồ án"));

        if (!"SUBMITTED".equals(submission.getStatus()) && !"LATE".equals(submission.getStatus())) {
            throw new RuntimeException("Sinh viên chưa nộp bài hoặc trạng thái không hợp lệ!");
        }

        Double score = Double.valueOf(gradeData.get("score").toString());
        if (score < 0 || score > 100) {
            throw new RuntimeException("Điểm số phải nằm trong khoảng 0 - 100");
        }

        submission.setScore(score);
        submission.setFeedback(gradeData.get("feedback").toString());
        submission.setStatus("GRADED");

        return submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public LectureMaterial uploadMaterial(Long courseId, Map<String, String> payload) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

        LectureMaterial material = LectureMaterial.builder()
                .course(course)
                .title(payload.get("title"))
                .fileUrl(payload.get("fileUrl"))
                .build();

        return lectureMaterialRepository.save(material);
    }
}