package dogveloper.vojoge.chatRoom.domain;

import dogveloper.vojoge.dog.domain.Dog;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "chatRoom")
public class ChatRoom {
    @Id
    private String id;

    @Indexed
    private Long chatroomNo;

    @Indexed
    private Long dogId;

    @Builder
    public ChatRoom(Long chatroomNo, Long dogId) {
        this.chatroomNo = chatroomNo;
        this.dogId = dogId;
    }
}
