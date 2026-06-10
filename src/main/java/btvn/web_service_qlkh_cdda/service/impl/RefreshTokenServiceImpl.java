package btvn.web_service_qlkh_cdda.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import btvn.web_service_qlkh_cdda.model.dto.request.RefreshTokenRequest;
import btvn.web_service_qlkh_cdda.model.dto.response.JWTResponse;
import btvn.web_service_qlkh_cdda.model.entity.RefreshToken;
import btvn.web_service_qlkh_cdda.repository.RefreshTokenRepository;
import btvn.web_service_qlkh_cdda.security.jwt.JWTProvider;
import btvn.web_service_qlkh_cdda.security.principal.CustomUserDetails;
import btvn.web_service_qlkh_cdda.service.RefreshTokenService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Value("${jwt-refresh-expire}")
    private Long refreshExpired;

    @Override
    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpired))
                .username(username)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            //remove refresh token từ table RefreshToken và save vào BlackListRefreshToken
            throw new RuntimeException("Refresh token đã hết hạn");
        }
        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }
        return token;
    }

    @Override
    public JWTResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String tokenStr = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("Token không tồn tại"));

        // Kiểm tra hết hạn và revoked
        verifyExpiration(refreshToken);

        // Cấp Access Token mới
        String newAccessToken = jwtProvider.generateToken(refreshToken.getUsername());
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(refreshToken.getUsername());

        return JWTResponse.builder()
                .username(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .enabled(userDetails.getEnabled())
                .authorities(userDetails.getAuthorities())
                .token(newAccessToken)
                .build();
    }
}
