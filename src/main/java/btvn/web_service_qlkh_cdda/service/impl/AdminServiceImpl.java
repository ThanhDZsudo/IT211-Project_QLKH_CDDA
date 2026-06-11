package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;

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
        return coursePage.getContent().stream()
                .map(c -> CourseDTO.builder()
                        .id(c.getId())
                        .courseCode(c.getCourseCode())
                        .courseName(c.getCourseName())
                        .credit(c.getCredit())
                        .build())
                .collect(Collectors.toList());
    }
}