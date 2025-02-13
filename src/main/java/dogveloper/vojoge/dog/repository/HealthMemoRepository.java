package dogveloper.vojoge.dog.repository;

import dogveloper.vojoge.dog.domain.HealthMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthMemoRepository extends JpaRepository<HealthMemo, Long> {
    List<HealthMemo> findByDogIdOrderByMemoDateDesc(Long dogId);
}
