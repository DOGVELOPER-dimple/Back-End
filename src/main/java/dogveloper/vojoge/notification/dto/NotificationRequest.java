package dogveloper.vojoge.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationRequest {
    @Schema(hidden = true)
    private Long userId;

    private Long dogId;
    private String message;
    private LocalDateTime scheduledTime;
    private boolean sent;
}
