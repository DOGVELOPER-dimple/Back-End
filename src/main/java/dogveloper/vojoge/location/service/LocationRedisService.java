package dogveloper.vojoge.location.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.service.DogService;
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


    private final DogService dogService;
    private final String GEO_KEY = "dogs:locations";

    public LocationRedisService(@Qualifier("redisTemplateLocation") RedisTemplate<String, String> redisTemplate, DogService dogService) {
        this.redisTemplate = redisTemplate;
        this.dogService = dogService;
    }

    public boolean saveUserLocation(Long dogId, double latitude, double longitude) {
        Dog dog = dogService.findById(dogId);
        if(!dogService.validation(dog)){
            log.error("not maching dog, uer");
            return false;
        }
        redisTemplate.opsForGeo().remove(GEO_KEY, String.valueOf(dog.getId()));
        Long added = redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), String.valueOf(dog.getId()));

        if (added == null || added == 0) {
            log.error("fail add not: dogId={}, latitude={}, longitude={}",
                    dog.getId(), latitude, longitude);
            return false;
        } else {
            log.info("save: dogId={}, latitude={}, longitude={}",
                    dog.getId(), latitude, longitude);
        }

        redisTemplate.expire(GEO_KEY, 20, TimeUnit.SECONDS);

        return true;
    }

    public List<ResponseRedisDto> getNearbyUsers(Long dogId, double latitude, double longitude) {
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(1000.0, Metrics.METERS));
        log.info("latitude={}, longitude={} radius={}", latitude, longitude, 1000);

        Dog dog = dogService.findById(dogId);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(GEO_KEY, circle);

        if (results == null || results.getContent().isEmpty()) {
            log.warn("not found latitude={}, longitude={}", latitude, longitude);
            return Collections.emptyList();
        }

        return results.getContent()
                .stream()
                .map(geoResult -> {
                    RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();
                    Long findDogId = Long.parseLong(location.getName());
                    Dog findDog = dogService.findById(findDogId);
                    List<Point> points = redisTemplate.opsForGeo().position(GEO_KEY, location.getName());
                    if (points == null || points.isEmpty()) {
                        return null;
                    }
                    Point point = points.get(0);
                    log.info("find data: userId={}, latitude={}, longitude={}",
                            location.getName(), point.getY(), point.getX());

                    return new ResponseRedisDto(
                            Long.valueOf(location.getName()),
                            findDog.getName(),
                            findDog.getAge(),
                            findDog.getPuppySpecies(),
                            findDog.getImage(),
                            point.getY(),
                            point.getX()
                    );
                })
                .filter(Objects::nonNull)
                .filter(dto -> !dog.getId().equals(dto.getId()))
                .collect(Collectors.toList());
    }


}
