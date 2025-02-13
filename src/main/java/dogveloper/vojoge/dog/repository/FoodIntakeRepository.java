package dogveloper.vojoge.dog.repository;

import dogveloper.vojoge.dog.domain.FoodIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodIntakeRepository extends JpaRepository<FoodIntake, Long> {
    List<FoodIntake> findByDogIdOrderByIntakeTimeDesc(Long dogId);
}
