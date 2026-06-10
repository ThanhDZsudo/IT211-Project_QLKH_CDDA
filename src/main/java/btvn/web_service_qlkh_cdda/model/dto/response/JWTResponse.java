package btvn.web_service_qlkh_cdda.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JWTResponse {
    private String username;
    private String fullName;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;
    private String token;
    private String refreshToken;
}
