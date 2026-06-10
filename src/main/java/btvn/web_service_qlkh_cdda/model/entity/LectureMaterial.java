package btvn.web_service_qlkh_cdda.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_material")
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class LectureMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String title;
    private String fileUrl; // Chứa link URL của tài liệu
}