package dogveloper.vojoge.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    private Long chatNo;
    private Long dogId;
}
