package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.request.CourseDTO;
import btvn.web_service_qlkh_cdda.model.entity.Course;
import btvn.web_service_qlkh_cdda.service.AdminService;
import btvn.web_service_qlkh_cdda.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    // ===================== CONTROLLER TEST 1 =====================
    @Test
    @WithMockUser(authorities = "ADMIN")
    void createCourse_Returns201_WhenSuccess() throws Exception {
        Course mockCourse = Course.builder().courseCode("INT1").courseName("Java Web").credit(3).build();
        when(adminService.createCourse(any(Course.class))).thenReturn(mockCourse);

        mockMvc.perform(post("/api/v1/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCourse)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseCode").value("INT1"));
    }

    // ===================== CONTROLLER TEST 2 =====================
    @Test
    @WithMockUser(authorities = "ADMIN")
    void createCourse_Returns400_WhenValidationFails() throws Exception {
        // Course với courseCode null sẽ fail validation @NotBlank
        Course invalidCourse = Course.builder().courseName("Java Web").credit(3).build();

        mockMvc.perform(post("/api/v1/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    // ===================== CONTROLLER TEST 3 =====================
    @Test
    @WithMockUser(authorities = "STUDENT") // Dùng role sai
    void createCourse_Returns403_WhenNotAdmin() throws Exception {
        Course mockCourse = Course.builder().courseCode("INT1").courseName("Java").credit(3).build();

        mockMvc.perform(post("/api/v1/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCourse)))
                .andExpect(status().isForbidden());
    }

    // ===================== CONTROLLER TEST 4 =====================
    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllCourses_Returns200_WithPaginatedList() throws Exception {
        List<CourseDTO> mockList = List.of(
                CourseDTO.builder().id(1L).courseCode("INT1").courseName("Java").credit(3).build(),
                CourseDTO.builder().id(2L).courseCode("INT2").courseName("MySQL").credit(3).build()
        );
        when(adminService.getAllCourses(0, 10)).thenReturn(mockList);

        mockMvc.perform(get("/api/v1/admin/courses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    // ===================== CONTROLLER TEST 5 =====================
    @Test
    void createCourse_Returns401_WhenNotAuthenticated() throws Exception {
        Course mockCourse = Course.builder().courseCode("INT1").courseName("Java").credit(3).build();

        mockMvc.perform(post("/api/v1/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCourse)))
                .andExpect(status().isUnauthorized());
    }
}