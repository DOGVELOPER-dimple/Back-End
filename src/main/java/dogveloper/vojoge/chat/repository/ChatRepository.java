package dogveloper.vojoge.chat.repository;

import dogveloper.vojoge.chat.domain.Chat;
import dogveloper.vojoge.dog.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByCreateDogOrJoinDog(Dog createDog, Dog joinDog);

    @Query("select c from Chat c where (c.createDog = :myDog and c.joinDog = :otherDog) or (c.createDog = :otherDog and c.joinDog = :myDog)")
    Optional<Chat> findActiveChat(@Param("myDog") Dog myDog, @Param("otherDog") Dog otherDog);
}
