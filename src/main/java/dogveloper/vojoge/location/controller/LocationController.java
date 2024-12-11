package dogveloper.vojoge.location.controller;

import dogveloper.vojoge.location.dto.ResponseRedisDto;
import dogveloper.vojoge.location.service.LocationRedisService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationRedisService locationRedisService;

    @Operation(summary = "주변 위치 가져오기", description = "주변 위치 가져오기")
    @GetMapping("/nearby")
    public List<ResponseRedisDto> getNearbyUsers(@RequestParam Long dogId,
                                                 @RequestParam double latitude,
                                                 @RequestParam double longitude) {
        if(!locationRedisService.saveUserLocation(dogId, latitude, longitude))
            return null;
        return locationRedisService.getNearbyUsers(dogId, latitude, longitude);
    }
}
