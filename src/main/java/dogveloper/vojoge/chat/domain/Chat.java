package dogveloper.vojoge.chat.domain;

import dogveloper.vojoge.chatRoom.dto.MyChatRoomResponse;
import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_dog")
    private Dog createDog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_dog")
    private Dog joinDog;

    private LocalDateTime localDateTime;

    public MyChatRoomResponse toResponse(Dog dog, Long cnt, String message) {
        return MyChatRoomResponse.builder()
                .chatRoomId(this.id)
                .joinDogId(dog.getId())
                .notReadMessageCnt(cnt)
                .lastContent(message)
                .build();
    }
}
