package btvn.web_service_qlkh_cdda.model.dto.request;
import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CourseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credit;
}