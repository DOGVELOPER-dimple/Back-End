package dogveloper.vojoge.walk.repository;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.walk.domain.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {
    List<Walk> findAllByDog(Dog dog);
}