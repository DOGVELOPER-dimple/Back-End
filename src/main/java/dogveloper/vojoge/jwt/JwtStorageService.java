package dogveloper.vojoge.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtStorageService {

    private static final Logger logger = LoggerFactory.getLogger(JwtStorageService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtStorageService(@Qualifier("redisTemplate2") RedisTemplate<String, String> redisTemplate, JwtTokenProvider jwtTokenProvider) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean saveRefreshToken(String email, String refreshToken, long validity) {
        try {
            redisTemplate.opsForValue().set("refresh:" + email, refreshToken, validity, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            logger.error("[JwtStorageService] Refresh Token 저장 오류: {}", e.getMessage());
            return false;
        }
    }

    public String getRefreshToken(String email) {
        try {
            return redisTemplate.opsForValue().get("refresh:" + email);
        } catch (Exception e) {
            logger.error("[JwtStorageService] Refresh Token 조회 오류: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteRefreshToken(String email) {
        try {
            redisTemplate.delete("refresh:" + email);
            return true;
        } catch (Exception e) {
            logger.error("[JwtStorageService] Refresh Token 삭제 오류: {}", e.getMessage());
            return false;
        }
    }
    public boolean addToBlacklist(String token) {
        try {
            long expiration = jwtTokenProvider.getExpiration(token); // ✅ 이제 오류 없음
            redisTemplate.opsForValue().set("blacklist:" + token, "invalid", expiration, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            logger.error("[JwtStorageService] Access Token 블랙리스트 추가 오류: {}", e.getMessage());
            return false;
        }
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }
}
