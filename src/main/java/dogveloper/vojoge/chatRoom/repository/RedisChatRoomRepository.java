package dogveloper.vojoge.chatRoom.repository;

import dogveloper.vojoge.chatRoom.domain.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface RedisChatRoomRepository extends CrudRepository<ChatRoom, String> {
    List<ChatRoom> findByChatroomNo(Long chatRoomNo);

    Optional<ChatRoom> findByChatroomNoAndDogId(Long chatRoomNo, Long DogId);
}
