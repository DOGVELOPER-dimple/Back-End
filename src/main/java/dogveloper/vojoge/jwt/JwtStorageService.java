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

    public JwtStorageService(@Qualifier("redisTemplate2") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 토큰 저장
    public boolean saveToken(String token, String email, long validityInMilliseconds) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }

            redisTemplate.opsForValue().set(token, email, validityInMilliseconds, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(email, token, validityInMilliseconds, TimeUnit.MILLISECONDS);

            if (redisTemplate.opsForValue().get(token) != null) {
                return true;
            } else {
                logger.warn("[JwtStorageService] 토큰 저장 실패 - email: {}", email);
                return false;
            }
        } catch (Exception e) {
            logger.error("[JwtStorageService] Redis에 토큰 저장 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    // 토큰으로 이메일 조회
    public String getEmailByToken(String token) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }
            return redisTemplate.opsForValue().get(token);
        } catch (Exception e) {
            logger.error("[JwtStorageService] Redis에서 이메일 조회 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    // 토큰 삭제
    public boolean deleteToken(String token) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }
            redisTemplate.delete(token);
            return true;
        } catch (Exception e) {
            logger.error("[JwtStorageService] Redis에서 토큰 삭제 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
