package btvn.web_service_qlkh_cdda.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_string", nullable = false, unique = true, length = 500)
    private String tokenString;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
}