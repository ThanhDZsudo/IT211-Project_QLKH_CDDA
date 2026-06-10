package btvn.web_service_qlkh_cdda.repository;

import btvn.web_service_qlkh_cdda.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
