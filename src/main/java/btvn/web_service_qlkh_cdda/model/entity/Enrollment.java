package btvn.web_service_qlkh_cdda.model.entity;

import jakarta.persistence.*; // BẮT BUỘC PHẢI LÀ JAKARTA
import lombok.*;
import java.time.LocalDateTime;

@Entity // NẾU THIẾU DÒNG NÀY SẼ BỊ LỖI NHƯ TRONG ẢNH
@Table(name = "enrollment")
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enroll_date")
    private LocalDateTime enrollDate;
}