package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.entity.Submission;
import java.util.Map;

public interface StudentService {
    void enrollCourse(Long courseId, String username);
    Submission submitProject(Long enrollmentId, Map<String, String> payload);
}