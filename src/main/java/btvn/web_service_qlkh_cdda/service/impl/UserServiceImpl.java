package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.dto.request.UserLogin;
import btvn.web_service_qlkh_cdda.model.dto.response.JWTResponse;
import btvn.web_service_qlkh_cdda.model.entity.Role;
import btvn.web_service_qlkh_cdda.model.entity.Users;
import btvn.web_service_qlkh_cdda.repository.RoleRepository;
import btvn.web_service_qlkh_cdda.repository.UserRepository;
import btvn.web_service_qlkh_cdda.security.jwt.JWTProvider;
import btvn.web_service_qlkh_cdda.security.principal.CustomUserDetails;
import btvn.web_service_qlkh_cdda.service.RefreshTokenService;
import btvn.web_service_qlkh_cdda.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Đã thêm biến này để hết đỏ
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Users registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        Role role = roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền STUDENT trong Database"));

        Users users = Users.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .enabled(true)
                .roles(List.of(role))
                .build();

        return userRepository.save(users);
    }

    @Override
    public JWTResponse login(UserLogin userLogin) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword())
            );
            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();

            String token = jwtProvider.generateToken(userDetails.getUsername());
            // ĐÃ SỬA: Lấy refreshToken dưới dạng String để khớp với DTO
            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername()).getToken();

            return JWTResponse.builder()
                    .username(userDetails.getUsername())
                    .fullName(userDetails.getFullName())
                    .enabled(userDetails.isEnabled())
                    .authorities(userDetails.getAuthorities())
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            log.info("Sai username hoặc password");
            throw new RuntimeException("Sai username hoặc password!");
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}