package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.domain.HealthMemo;
import dogveloper.vojoge.dog.dto.HealthMemoDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.dog.repository.HealthMemoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthMemoService {
    private final HealthMemoRepository healthMemoRepository;
    private final DogRepository dogRepository;

    @Transactional
    public HealthMemoDTO saveHealthMemo(Long dogId, HealthMemoDTO dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("반려견을 찾을 수 없습니다."));

        HealthMemo healthMemo = new HealthMemo(dog, dto.getTitle(), dto.getMemoDate(), dto.getNotes());
        HealthMemo savedMemo = healthMemoRepository.save(healthMemo);

        // ✅ "심장사상충" 접종이면 반려견 모델 자동 업데이트
        if ("심장사상충".equals(dto.getTitle())) {
            dog.setRecentCheckupDate(dto.getMemoDate());
            dogRepository.save(dog);
        }

        return HealthMemoDTO.fromEntity(savedMemo);
    }

    public List<HealthMemoDTO> getHealthMemosByDog(Long dogId) {
        return healthMemoRepository.findByDogIdOrderByMemoDateDesc(dogId)
                .stream()
                .map(HealthMemoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHealthMemo(Long memoId) {
        healthMemoRepository.deleteById(memoId);
    }
}
