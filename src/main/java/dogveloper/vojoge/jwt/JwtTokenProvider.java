package dogveloper.vojoge.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long VALIDITY_IN_MILLISECONDS = 3600000;

    private final JwtStorageService jwtStorageService;

    // JwtStorageService 의존성 주입
    public JwtTokenProvider(JwtStorageService jwtStorageService) {
        this.jwtStorageService = jwtStorageService;
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // Redis에 저장
        boolean isSaved = jwtStorageService.saveToken(token, email, VALIDITY_IN_MILLISECONDS);
        if (isSaved) {
            System.out.println("Token successfully saved in Redis.");
        } else {
            System.err.println("Token was not saved in Redis.");
        }

        return token;
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
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
