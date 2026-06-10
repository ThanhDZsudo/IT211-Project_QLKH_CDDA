package btvn.web_service_qlkh_cdda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import btvn.web_service_qlkh_cdda.model.entity.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    // THÊM DÒNG NÀY VÀO LÀ HẾT LỖI ĐỎ Ở USERSERVICEIMPL
    boolean existsByUsername(String username);
}