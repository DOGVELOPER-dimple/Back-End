package dogveloper.vojoge.chat.dto;

import dogveloper.vojoge.chat.mongo.Chatting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private String id;
    private Long chatRoomNo;
    private Long senderNo;
    private String senderName;
    private String contentType;
    private String content;
    private LocalDateTime sendDate;
    private boolean isRead;
    private boolean isMine;

    public ChatResponseDto(Chatting chatting, Long dogId, String dogName) {
        this.id = chatting.getId();
        this.chatRoomNo = chatting.getChatRoomNo();
        this.senderNo = dogId;
        this.senderName = dogName;
        this.content = chatting.getContent();
        this.sendDate = chatting.getSendDate();
        this.isRead = chatting.isRead();
        this.isMine = chatting.getSenderId().equals(dogId);
    }
}