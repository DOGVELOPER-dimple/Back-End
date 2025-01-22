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

    private Long userId;

    private Long dogId;

    private String message;

    private LocalDateTime scheduledTime;

    private boolean sent; // 알림이 전송되었는지 여부
}
