package btvn.web_service_qlkh_cdda.repository;

import btvn.web_service_qlkh_cdda.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByTokenString(String tokenString);
}