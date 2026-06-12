package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.repository.CourseRepository;
import btvn.web_service_qlkh_cdda.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void createCourse_Success() {
        // Chuẩn bị dữ liệu (Arrange)
        Course mockCourse = Course.builder().courseCode("INT1").courseName("Java").credit(3).build();
        when(courseRepository.existsByCourseCode("INT1")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        // Thực thi (Act)
        Course result = adminService.createCourse(mockCourse);

        // Kiểm chứng (Assert)
        assertNotNull(result);
        assertEquals("INT1", result.getCourseCode());
        verify(courseRepository, times(1)).save(mockCourse);
    }

    @Test
    void createCourse_ThrowsException_WhenCodeExists() {
        Course mockCourse = Course.builder().courseCode("INT1").build();
        when(courseRepository.existsByCourseCode("INT1")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.createCourse(mockCourse);
        });

        assertEquals("Mã khóa học đã tồn tại!", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }
}