package dogveloper.vojoge.walk.repository;

import dogveloper.vojoge.walk.domain.WalkActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WalkActivityRepository extends JpaRepository<WalkActivity, Long> {
    List<WalkActivity> findByDogIdAndTimestampBetweenOrderByTimestampAsc(Long dogId, LocalDateTime start, LocalDateTime end);
}
