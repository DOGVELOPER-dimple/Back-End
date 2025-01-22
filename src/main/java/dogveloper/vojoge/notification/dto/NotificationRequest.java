package dogveloper.vojoge.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationRequest {
    private Long dogId; // 강아지 ID
    private String message; // 알림 메시지
    private LocalDateTime scheduledTime; // 스케줄 시간
    private boolean sent;
}
