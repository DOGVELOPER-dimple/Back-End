package dogveloper.vojoge.chat.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoChatRepository extends MongoRepository<Chatting, String> {
    List<Chatting> findByChatRoomNo(Long chatNo);

    Page<Chatting> findByChatRoomNoOrderBySendDateDesc(Long chatRoomNo, Pageable pageable);
}
