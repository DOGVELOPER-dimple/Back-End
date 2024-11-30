package dogveloper.vojoge.chatRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomResponse {

    private Long chatRoomId;

    private Long joinDogId;

    private Long notReadMessageCnt;

    private String lastContent;
}