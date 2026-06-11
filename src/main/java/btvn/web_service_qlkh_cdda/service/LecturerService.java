package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.entity.LectureMaterial;
import btvn.web_service_qlkh_cdda.model.entity.Submission;
import java.util.Map;

public interface LecturerService {
    Submission gradeSubmission(Long submissionId, Map<String, Object> gradeData);
    LectureMaterial uploadMaterial(Long courseId, Map<String, String> payload);
}