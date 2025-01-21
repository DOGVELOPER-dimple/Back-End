package dogveloper.vojoge.walk.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.dog.service.DogService;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import dogveloper.vojoge.walk.domain.Walk;
import dogveloper.vojoge.walk.dto.request.RequestWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDetailDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkHistoryDto;
import dogveloper.vojoge.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalkService {
    private final WalkRepository walkRepository;
    private final DogService dogService;
    private final UserService userService;
    public Walk createWalk(Long dogId, RequestWalkDto requestWalkDto){
        User user = userService.getAuthenticatedUser();
        Dog dog = dogService.findById(dogId);

        if(dog != null && dog.getUser().equals(user)) {
            Walk walk = requestWalkDto.toEntity(dog);
            return walkRepository.save(walk);
        }
        return null;
    }

    public Walk findById(Long id){
        return walkRepository.findById(id).orElse(null);
    }

    public List<ResponseWalkHistoryDto> findAllByDogId(Long dogId){
        Dog dog = dogService.findById(dogId);
        if(dog != null){
            List<Walk> walks = walkRepository.findAllByDog(dog);
            return walks.stream()
                    .map(Walk :: toResponseHistoryDto)
                    .toList();
        }
        return null;
    }

    public ResponseWalkDetailDto findDetailWalk(Long walkId){
        Walk walk = findById(walkId);
        if(walk != null){
            return walk.toResponseDetailDto();
        }
        else{
            return null;
        }
    }

    public boolean deleteWalk(Long walkId){
        Walk walk = findById(walkId);
        if(walk != null && dogService.validation(walk.getDog())){
            walkRepository.deleteById(walkId);
            return true;
        }else{
            return false;
        }
    }
}
