package btvn.web_service_qlkh_cdda.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTProvider {
    @Value("${jwt-secret}")
    private String jwtSecret;
    @Value("${jwt-expire}")
    private Long jwtExpired;


    public String generateToken(String username){
        try{
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Date now = new Date();
            Date expire = new Date(now.getTime()+jwtExpired);

            return Jwts.builder()
                    .subject(username)
                    .signWith(key)
                    .issuedAt(now)
                    .expiration(expire)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token){
        try{
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (UnsupportedJwtException e) {
            log.info("Hệ thống không hỗ trợ jwt");
            throw new RuntimeException("Hệ thống không hỗ trợ jwt ",e);
        } catch (MalformedJwtException e) {
            log.info("Chuỗi jwt không đúng");
            throw new RuntimeException("Chuỗi jwt không đúng", e);
        } catch (ExpiredJwtException e){
            log.info("Chuỗi jwt hết hạn");
            throw new RuntimeException("Chuỗi jwt hết hạn ",e);
        }catch (SignatureException e){
            log.info("Sai chữ ký JWT");
            throw new RuntimeException("Sai chữ ký JWT ",e);
        }catch (IllegalArgumentException e){
            log.info("Chuỗi JWT rỗng");
            throw new RuntimeException("Chuỗi jwt rỗng ",e);
        }catch (JwtException e){
            log.info("JWT không hợp lệ");
            throw new RuntimeException("JWT không hợp lệ", e);
        }
    }

    public String getUsernameFromToken(String token){
        try{
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Không lấy được username từ chuỗi token");
        }
    }
}
