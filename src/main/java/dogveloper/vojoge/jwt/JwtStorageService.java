package dogveloper.vojoge.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtStorageService {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtStorageService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 토큰 저장
    public boolean saveToken(String token, String email, long validityInMilliseconds) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }

            // 저장 전 로그 출력
            System.out.println("Saving token: " + token + " for email: " + email);
            System.out.println("TTL: " + validityInMilliseconds + "ms");

            // Redis에 저장
            redisTemplate.opsForValue().set(token, email, validityInMilliseconds, TimeUnit.MILLISECONDS);

            System.out.println("Redis에 저장된 값 확인: " + redisTemplate.opsForValue().get(token));

            // 저장 확인
            String storedValue = redisTemplate.opsForValue().get(token);
            if (storedValue != null) {
                System.out.println("Token saved successfully for email: " + storedValue);
                return true;
            } else {
                System.err.println("Token was not saved properly.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error while saving token to Redis: " + e.getMessage());
            return false;
        }
    }

    // 토큰으로 이메일 조회
    public String getEmailByToken(String token) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }

            // Redis에서 값 조회
            String email = redisTemplate.opsForValue().get(token);
            System.out.println("Retrieved email for token: " + token + " is " + email);
            return email;
        } catch (Exception e) {
            System.err.println("Error while retrieving email by token: " + e.getMessage());
            return null;
        }
    }

    // 토큰 삭제
    public boolean deleteToken(String token) {
        try {
            if (redisTemplate == null) {
                throw new IllegalStateException("RedisTemplate is not initialized.");
            }

            // Redis에서 키 삭제
            redisTemplate.delete(token);
            System.out.println("Token deleted: " + token);
            return true;
        } catch (Exception e) {
            System.err.println("Error while deleting token from Redis: " + e.getMessage());
            return false;
        }
    }
}
