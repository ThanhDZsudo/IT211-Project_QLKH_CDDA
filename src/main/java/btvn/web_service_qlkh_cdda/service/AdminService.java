package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import java.util.List;

public interface AdminService {
    Course createCourse(Course course);
    List<CourseDTO> getAllCourses(int page, int size);
}