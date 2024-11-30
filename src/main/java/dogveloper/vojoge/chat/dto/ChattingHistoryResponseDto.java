package dogveloper.vojoge.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingHistoryResponseDto {

    private String dogName;

    private List<ChatResponseDto> chatList;
}
