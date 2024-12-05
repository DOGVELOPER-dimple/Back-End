package dogveloper.vojoge.dog.repository;

import dogveloper.vojoge.dog.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
}