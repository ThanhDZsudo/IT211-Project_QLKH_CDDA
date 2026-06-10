package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.dto.request.RefreshTokenRequest;
import btvn.web_service_qlkh_cdda.model.dto.response.JWTResponse;
import btvn.web_service_qlkh_cdda.model.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
    RefreshToken verifyExpiration(RefreshToken token);
    JWTResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
