package btvn.web_service_qlkh_cdda.repository;

import btvn.web_service_qlkh_cdda.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudent_UserIdAndCourse_Id(Long studentId, Long courseId);
}