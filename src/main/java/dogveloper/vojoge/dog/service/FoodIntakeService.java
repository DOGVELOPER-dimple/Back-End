package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.domain.FoodIntake;
import dogveloper.vojoge.dog.dto.FoodIntakeDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.dog.repository.FoodIntakeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodIntakeService {
    private final FoodIntakeRepository foodIntakeRepository;
    private final DogRepository dogRepository;

    @Transactional
    public FoodIntakeDTO saveFoodIntake(Long dogId, FoodIntakeDTO dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("반려견을 찾을 수 없습니다."));

        FoodIntake foodIntake = dto.toEntity(dog);
        return FoodIntakeDTO.fromEntity(foodIntakeRepository.save(foodIntake));
    }

    public List<FoodIntakeDTO> getFoodIntakeByDog(Long dogId) {
        return foodIntakeRepository.findByDogIdOrderByIntakeTimeDesc(dogId)
                .stream()
                .map(FoodIntakeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public FoodIntakeDTO updateFoodIntake(Long intakeId, FoodIntakeDTO dto) {
        FoodIntake existingIntake = foodIntakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntityNotFoundException("급여량 데이터를 찾을 수 없습니다."));

        if (dto.getAmount() != null) existingIntake.setAmount(dto.getAmount());
        if (dto.getIntakeTime() != null) existingIntake.setIntakeTime(dto.getIntakeTime());

        return FoodIntakeDTO.fromEntity(foodIntakeRepository.save(existingIntake));
    }

    @Transactional
    public void deleteFoodIntake(Long intakeId) {
        foodIntakeRepository.deleteById(intakeId);
    }
}
