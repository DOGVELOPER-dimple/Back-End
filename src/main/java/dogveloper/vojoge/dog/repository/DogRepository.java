package dogveloper.vojoge.dog.repository;

import dogveloper.vojoge.dog.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByUserId(Long userId);
}
