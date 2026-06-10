package btvn.web_service_qlkh_cdda.repository;

import btvn.web_service_qlkh_cdda.model.entity.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {
}