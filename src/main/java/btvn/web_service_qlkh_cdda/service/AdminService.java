package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.model.entity.Users;

import java.util.List;

public interface AdminService {
    // Course management
    Course createCourse(Course course);
    List<CourseDTO> getAllCourses(int page, int size);

    // User management (FR-05)
    List<Users> getAllUsers(int page, int size);
    Users getUserByUsername(String username);
    Users createUser(UserDTO userDTO);
    Users updateUser(Long userId, UserDTO userDTO);
    void deactivateUser(Long userId);
}