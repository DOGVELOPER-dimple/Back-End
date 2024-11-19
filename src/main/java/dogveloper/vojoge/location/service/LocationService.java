package dogveloper.vojoge.location.service;

import dogveloper.vojoge.dog.service.DogService;
import dogveloper.vojoge.location.domain.Location;
import dogveloper.vojoge.location.dto.RequestWalkLocationDto;
import dogveloper.vojoge.location.dto.ResponseLocationDto;
import dogveloper.vojoge.location.repository.LocationRepository;
import dogveloper.vojoge.walk.domain.Walk;
import dogveloper.vojoge.walk.dto.response.ResponseAddWalkDto;
import dogveloper.vojoge.walk.service.WalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final WalkService walkService;

    public boolean addLocation(Walk walk, List<RequestWalkLocationDto> requestWalkLocationDtos){
        if(walk == null)
            return false;
        for(RequestWalkLocationDto requestWalkLocationDto : requestWalkLocationDtos){
            Location location = requestWalkLocationDto.toEntity(walk);
            locationRepository.save(location);
        }
        return true;
    }

    public List<ResponseLocationDto> findAllByWalk(Long walk_id){
        Walk walk = walkService.findById(walk_id);
        return locationRepository.findAllByWalk(walk).stream()
                .map(Location :: toResponseLocationDto)
                .toList();
    }
}
