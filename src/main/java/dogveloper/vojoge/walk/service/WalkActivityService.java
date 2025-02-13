package dogveloper.vojoge.walk.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.walk.domain.WalkActivity;
import dogveloper.vojoge.walk.dto.WalkActivityDTO;
import dogveloper.vojoge.walk.repository.WalkActivityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkActivityService {
    private final WalkActivityRepository walkActivityRepository;
    private final DogRepository dogRepository;

    @Transactional
    public WalkActivityDTO saveWalkActivity(Long dogId, WalkActivityDTO dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("반려견을 찾을 수 없습니다."));

        WalkActivity activity = new WalkActivity(dog, dto.getTimestamp(), dto.getDistance(), dto.getBowelMovements());
        return WalkActivityDTO.fromEntity(walkActivityRepository.save(activity));
    }

    public List<WalkActivityDTO> getWalkActivities(Long dogId, String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period) {
            case "1일":
                startDate = now.minus(1, ChronoUnit.DAYS);
                break;
            case "1주":
                startDate = now.minus(7, ChronoUnit.DAYS);
                break;
            case "1개월":
                startDate = now.minus(1, ChronoUnit.MONTHS);
                break;
            case "1년":
                startDate = now.minus(1, ChronoUnit.YEARS);
                break;
            default:
                throw new IllegalArgumentException("올바른 기간을 입력해주세요. (1일, 1주, 1개월, 1년)");
        }

        return walkActivityRepository.findByDogIdAndTimestampBetweenOrderByTimestampAsc(dogId, startDate, now)
                .stream()
                .map(WalkActivityDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWalkActivity(Long activityId) {
        walkActivityRepository.deleteById(activityId);
    }
}
