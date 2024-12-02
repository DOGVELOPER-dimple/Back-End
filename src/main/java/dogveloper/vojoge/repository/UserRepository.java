package dogveloper.vojoge.repository;

import dogveloper.vojoge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySub(String sub);
    Optional<User> findByEmail(String email);

}
