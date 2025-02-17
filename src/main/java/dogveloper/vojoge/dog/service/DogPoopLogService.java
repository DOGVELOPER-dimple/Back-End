package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.domain.DogPoopLog;
import dogveloper.vojoge.dog.dto.DogPoopLogDTO;
import dogveloper.vojoge.dog.repository.DogPoopLogRepository;
import dogveloper.vojoge.dog.repository.DogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogPoopLogService {
    private final DogPoopLogRepository dogPoopLogRepository;
    private final DogRepository dogRepository;

    @Transactional
    public DogPoopLogDTO addPoopLog(Long dogId, DogPoopLogDTO dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("반려견을 찾을 수 없습니다."));

        DogPoopLog log = DogPoopLog.builder()
                .dog(dog)
                .poopTime(dto.getPoopTime())
                .poopType(dto.getPoopType())
                .build();

        return DogPoopLogDTO.fromEntity(dogPoopLogRepository.save(log));
    }

    public List<DogPoopLogDTO> getPoopLogs(Long dogId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return dogPoopLogRepository.findByDogIdAndPoopTimeBetween(dogId, start, end)
                .stream()
                .map(DogPoopLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePoopLog(Long logId) {
        dogPoopLogRepository.deleteById(logId);
    }
}
