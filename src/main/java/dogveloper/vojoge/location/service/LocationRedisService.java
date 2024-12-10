package dogveloper.vojoge.location.service;

import dogveloper.vojoge.location.dto.RequestWalkLocationDto;
import dogveloper.vojoge.location.dto.ResponseRedisDto;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LocationRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final String GEO_KEY = "users:locations";

    public LocationRedisService(@Qualifier("redisTemplateLocation") RedisTemplate<String, String> redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    public void saveUserLocation(double latitude, double longitude) {
        User user = userService.getAuthenticatedUser();

        Long added = redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), String.valueOf(user.getId()));

        if (added == null || added == 0) {
            log.error("Failed to save user location to Redis: userId={}, latitude={}, longitude={}",
                    user.getId(), latitude, longitude);
        } else {
            log.info("Saved user location: userId={}, latitude={}, longitude={}",
                    user.getId(), latitude, longitude);
        }

        // 바로 저장된 데이터 확인
        List<Point> savedPoints = redisTemplate.opsForGeo().position(GEO_KEY, String.valueOf(user.getId()));
        if (savedPoints != null && !savedPoints.isEmpty()) {
            Point savedPoint = savedPoints.get(0);
            log.info("Retrieved saved location: userId={}, latitude={}, longitude={}",
                    user.getId(),
                    Math.round(savedPoint.getY() * 1e6) / 1e6,
                    Math.round(savedPoint.getX() * 1e6) / 1e6
            );
        } else {
            log.warn("No location found for userId={} immediately after saving.", user.getId());
        }

        redisTemplate.expire(GEO_KEY, 20, TimeUnit.SECONDS);
    }

    public List<ResponseRedisDto> getNearbyUsers(double latitude, double longitude) {
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(1000.0, Metrics.METERS));
        log.info("Circle Center: Latitude={}, Longitude={}, Radius={} meters", latitude, longitude, 1000);

        User user = userService.getAuthenticatedUser();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(GEO_KEY, circle);

        if (results == null || results.getContent().isEmpty()) {
            log.warn("No nearby users found for latitude={}, longitude={}", latitude, longitude);
            return Collections.emptyList();
        }

        log.info("Found {} results within radius.", results.getContent().size());

        return results.getContent()
                .stream()
                .map(geoResult -> {
                    RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();

                    List<Point> points = redisTemplate.opsForGeo().position(GEO_KEY, location.getName());
                    if (points == null || points.isEmpty()) {
                        log.warn("No Point found for User {}", location.getName());
                        return null;
                    }

                    Point point = points.get(0);
                    log.info("User found: userId={}, latitude={}, longitude={}",
                            location.getName(), point.getY(), point.getX());

                    return new ResponseRedisDto(
                            Long.valueOf(location.getName()),
                            point.getY(),
                            point.getX()
                    );
                })
                .filter(Objects::nonNull)
                .filter(dto -> !user.getId().equals(dto.getId()))
                .collect(Collectors.toList());
    }


}
