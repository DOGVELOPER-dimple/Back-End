package dogveloper.vojoge.location.repository;

import dogveloper.vojoge.location.domain.Location;
import dogveloper.vojoge.walk.domain.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByWalk(Walk walk);
}
