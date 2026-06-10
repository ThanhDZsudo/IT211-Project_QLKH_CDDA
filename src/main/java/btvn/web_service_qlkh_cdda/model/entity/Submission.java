package btvn.web_service_qlkh_cdda.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission")
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    private String reportUrl;
    private String githubUrl;
    private Double score;
    private String feedback;

    // BỔ SUNG DÒNG NÀY VÀO LÀ HẾT LỖI NGAY
    private String status;
}