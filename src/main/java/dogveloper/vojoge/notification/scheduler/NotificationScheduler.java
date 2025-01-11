package dogveloper.vojoge.notification.scheduler;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.repository.NotificationRepository;
import dogveloper.vojoge.notification.controller.NotificationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationController notificationController;

    @Scheduled(fixedRate = 100000) // 5초마다 실행
    public void sendScheduledNotifications() {
        List<Notification> notifications = notificationRepository.findByScheduledTimeBeforeAndSent(LocalDateTime.now(), false);

        notifications.forEach(notification -> {
            log.info("Sending notification to dog ID {}: {}", notification.getDogId(), notification.getMessage());
            notificationController.notifySubscribers(notification);
            notification.setSent(true); // 알림 전송 완료
            notificationRepository.save(notification);
        });
    }
}
