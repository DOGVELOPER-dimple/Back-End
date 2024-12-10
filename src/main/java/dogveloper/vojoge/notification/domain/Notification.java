package dogveloper.vojoge.notification.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dogId; // 특정 강아지를 위한 알림

    private String message; // 알림 내용

    private LocalDateTime scheduledTime; // 알림 스케줄 시간

    private boolean sent; // 알림이 전송되었는지 여부
}
