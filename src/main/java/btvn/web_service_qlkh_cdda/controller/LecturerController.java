package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.entity.LectureMaterial;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import btvn.web_service_qlkh_cdda.service.LecturerService;
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

    private final LecturerService lecturerService; // Gọi qua Service chuẩn kiến trúc

    @PutMapping("/grades/{submissionId}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<ApiDataResonse<Submission>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradeData) {

        Submission saved = lecturerService.gradeSubmission(submissionId, gradeData);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Chấm điểm thành công", saved, null, HttpStatus.OK));
    }

    @PostMapping("/materials/{courseId}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<ApiDataResonse<LectureMaterial>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> payload) {

        LectureMaterial saved = lecturerService.uploadMaterial(courseId, payload);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Tải tài liệu bài giảng thành công", saved, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }
}