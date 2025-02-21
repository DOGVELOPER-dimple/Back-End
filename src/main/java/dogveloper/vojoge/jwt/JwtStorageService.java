package dogveloper.vojoge.jwt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtStorageService {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtStorageService(@Qualifier("redisTemplate2") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean saveToken(String token, String email, long validityInMilliseconds) {
        try {
            redisTemplate.opsForValue().set(token, email, validityInMilliseconds, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public boolean deleteToken(String token) {
        redisTemplate.delete(token);
        return true;
    }
}
