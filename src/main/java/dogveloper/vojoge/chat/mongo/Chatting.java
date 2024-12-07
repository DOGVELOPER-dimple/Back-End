package dogveloper.vojoge.chat.mongo;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatting")
public class Chatting {
    @Id
    private String id;

    private Long chatRoomNo;

    private Long senderId;

    private String senderName;

    private String content;

    private LocalDateTime sendDate;

    private boolean isRead;
}


