package dogveloper.vojoge.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationRequest {
    private Long userId;
    private Long dogId;
    private String message;
    private LocalDateTime scheduledTime;
    private boolean sent;
}
