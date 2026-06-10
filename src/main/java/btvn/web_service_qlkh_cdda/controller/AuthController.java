package btvn.web_service_qlkh_cdda.controller;

import btvn.web_service_qlkh_cdda.model.dto.request.RefreshTokenRequest;
import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.dto.request.UserLogin;
import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.dto.response.JWTResponse;
import btvn.web_service_qlkh_cdda.model.entity.TokenBlacklist;
import btvn.web_service_qlkh_cdda.repository.TokenBlacklistRepository;
import btvn.web_service_qlkh_cdda.service.RefreshTokenService;
import btvn.web_service_qlkh_cdda.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiDataResonse<JWTResponse>> login(@RequestBody UserLogin userLogin) {
        JWTResponse response = userService.login(userLogin);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Đăng nhập thành công", response, null, HttpStatus.OK));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiDataResonse<?>> register(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);
        return new ResponseEntity<>(new ApiDataResonse<>(true, "Đăng ký tài khoản Sinh viên thành công", null, null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiDataResonse<JWTResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        JWTResponse response = refreshTokenService.refreshToken(request);
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Làm mới token thành công", response, null, HttpStatus.OK));
    }

    // ĐÃ FIX: Thu hồi Token vào danh sách đen (FR-03)
    @PostMapping("/logout")
    public ResponseEntity<ApiDataResonse<?>> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .tokenString(token)
                    .revokedAt(LocalDateTime.now())
                    .build();
            tokenBlacklistRepository.save(blacklist);
        }
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Đăng xuất và thu hồi Token thành công", null, null, HttpStatus.OK));
    }

    // ĐÃ THÊM: Đổi mật khẩu cho người dùng đã xác thực (FR-10)
    @PostMapping("/change-password")
    public ResponseEntity<ApiDataResonse<?>> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        userService.changePassword(authentication.getName(), request.get("oldPassword"), request.get("newPassword"));
        return ResponseEntity.ok(new ApiDataResonse<>(true, "Đổi mật khẩu thành công", null, null, HttpStatus.OK));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}