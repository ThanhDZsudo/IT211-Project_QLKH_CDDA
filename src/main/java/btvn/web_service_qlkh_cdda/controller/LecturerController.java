package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.LectureMaterial;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.repository.LectureMaterialRepository;
import btvn.web_service_qlkh_cdda.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LecturerController {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final LectureMaterialRepository lectureMaterialRepository;

    @PutMapping("/grades/{submissionId}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<ApiDataResonse<Submission>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradeData) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp đồ án"));

        if (!"SUBMITTED".equals(submission.getStatus()) && !"LATE".equals(submission.getStatus())) {
            throw new RuntimeException("Sinh viên chưa nộp bài hoặc trạng thái không hợp lệ!");
        }

        Double score = Double.valueOf(gradeData.get("score").toString());
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Điểm số phải nằm trong khoảng 0 - 100");
        }

        submission.setScore(score);
        submission.setFeedback(gradeData.get("feedback").toString());
        submission.setStatus("GRADED");

        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Chấm điểm thành công", saved, null, HttpStatus.OK));
    }

    // ĐÃ THÊM: Tải lên tài liệu bài giảng (FR-09)
    @PostMapping("/materials/{courseId}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<ApiDataResonse<LectureMaterial>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> payload) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

        LectureMaterial material = LectureMaterial.builder()
                .course(course)
                .title(payload.get("title"))
                .fileUrl(payload.get("fileUrl")) // Chứa link file trên cloud
                .build();

        LectureMaterial saved = lectureMaterialRepository.save(material);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Tải tài liệu bài giảng thành công", saved, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }
}