package dogveloper.vojoge.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Key refreshSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long ACCESS_TOKEN_VALIDITY = 3600000;  // 1시간
    private static final long REFRESH_TOKEN_VALIDITY = 1209600000; // 2주

    private final JwtStorageService jwtStorageService;

    public JwtTokenProvider(JwtStorageService jwtStorageService) {
        this.jwtStorageService = jwtStorageService;
    }

    public String createAccessToken(String email) {
        return generateToken(email, secretKey, ACCESS_TOKEN_VALIDITY);
    }

    public String createRefreshToken(String email) {
        String refreshToken = generateToken(email, refreshSecretKey, REFRESH_TOKEN_VALIDITY);
        jwtStorageService.saveToken(refreshToken, email, REFRESH_TOKEN_VALIDITY);
        return refreshToken;
    }

    private String generateToken(String email, Key key, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseToken(token, secretKey);
    }

    public String getEmailFromRefreshToken(String token) {
        return parseToken(token, refreshSecretKey);
    }

    private String parseToken(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            Key key = isRefreshToken ? refreshSecretKey : secretKey;
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
