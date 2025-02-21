package dogveloper.vojoge.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_VALIDITY = 1800000; // 1시간
    private static final long REFRESH_TOKEN_VALIDITY = 1209600000; // 14일

    private final JwtStorageService jwtStorageService;

    public JwtTokenProvider(@Lazy JwtStorageService jwtStorageService) {
        this.jwtStorageService = jwtStorageService;
    }

    public String createToken(String email) {
        return generateToken(email, ACCESS_TOKEN_VALIDITY);
    }

    public String createRefreshToken(String email) {
        String refreshToken = generateToken(email, REFRESH_TOKEN_VALIDITY);
        jwtStorageService.saveRefreshToken(email, refreshToken, REFRESH_TOKEN_VALIDITY);
        return refreshToken;
    }

    private String generateToken(String email, long validity) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("[JwtTokenProvider] 토큰 검증 실패: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("[JwtTokenProvider] 만료된 토큰: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("[JwtTokenProvider] JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }
    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            logger.error("[JwtTokenProvider] 토큰 만료 시간 조회 실패: {}", e.getMessage());
            return 0;
        }
    }
}
