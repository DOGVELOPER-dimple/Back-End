package dogveloper.vojoge.chat.dto;

import dogveloper.vojoge.chat.mongo.Chatting;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    @Setter
    private String id;
    private Long chatNo;
    private String content;
    private Long senderId;
    private String senderName;
    private LocalDateTime sendTime;
    private boolean isRead;

    public void setSendTimeAndSender(LocalDateTime sendTime, String senderName, boolean isRead){
        this.senderName = senderName;
        this.sendTime = sendTime;
        this.isRead = isRead;
    }

    public void setId(String id) {this.id = id;}

    public Chatting toEntity(Long dogId) {
        return Chatting.builder()
                .chatRoomNo(chatNo)
                .senderId(dogId)
                .senderName(senderName)
                .content(content)
                .sendDate(sendTime)
                .isRead(isRead)
                .build();
    }
}


