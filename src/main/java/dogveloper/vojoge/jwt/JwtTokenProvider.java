package dogveloper.vojoge.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long VALIDITY_IN_MILLISECONDS = 3600000;

    private final JwtStorageService jwtStorageService;

    public JwtTokenProvider(JwtStorageService jwtStorageService) {
        this.jwtStorageService = jwtStorageService;
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();

            jwtStorageService.saveToken(token, email, VALIDITY_IN_MILLISECONDS);
            return token;
        } catch (Exception e) {
            logger.error("[JwtTokenProvider] 토큰 생성 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    public String getEmailFromToken(String token) {
        logger.info("[JwtTokenProvider] 토큰에서 이메일 추출 시작");
        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            logger.info("[JwtTokenProvider] 토큰에서 추출한 이메일: {}", email);
            return email;
        } catch (JwtException e) {
            logger.error("[JwtTokenProvider] 토큰에서 이메일 추출 실패: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}