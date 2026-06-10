package btvn.web_service_qlkh_cdda.security.jwt;

import btvn.web_service_qlkh_cdda.repository.TokenBlacklistRepository;
import btvn.web_service_qlkh_cdda.security.principal.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    // Inject Repository để kiểm tra Blacklist
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token != null && jwtProvider.validateToken(token)) {
                // KIỂM TRA BLACKLIST
                boolean isBlacklisted = tokenBlacklistRepository.findByTokenString(token).isPresent();
                if (isBlacklisted) {
                    log.warn("Truy cập bị từ chối: Token đã nằm trong Blacklist.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Token đã bị thu hồi. Vui lòng đăng nhập lại.\"}");
                    return; // Chặn đứng luồng request, không cho đi tiếp
                }

                String username = jwtProvider.getUsernameFromToken(token);
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Bắt các ngoại lệ do JWTProvider ném ra (như ExpiredJwtException)
            log.error("Lỗi xác thực Token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        // Lưu ý: Chuỗi "Bearer " phải có dấu cách ở cuối
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}