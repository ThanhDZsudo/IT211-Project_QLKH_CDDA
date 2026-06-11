package btvn.web_service_qlkh_cdda.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Mã khóa học không được trống")
    @Column(name = "course_code", unique = true, nullable = false, length = 50)
    private String courseCode;

    @NotBlank(message = "Tên khóa học không được trống")
    @Column(name = "course_name", nullable = false, length = 150)
    private String courseName;

    @NotNull(message = "Số tín chỉ không được trống")
    private Integer credit;
}