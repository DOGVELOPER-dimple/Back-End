package dogveloper.vojoge.dog.repository;

import dogveloper.vojoge.dog.domain.DogPoopLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DogPoopLogRepository extends JpaRepository<DogPoopLog, Long> {
    List<DogPoopLog> findByDogIdAndPoopTimeBetween(Long dogId, LocalDateTime start, LocalDateTime end);
}
