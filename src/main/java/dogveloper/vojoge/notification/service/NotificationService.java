package dogveloper.vojoge.notification.service;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification saveNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .dogId(request.getDogId())
                .message(request.getMessage())
                .scheduledTime(request.getScheduledTime())
                .sent(false)
                .build();
        return notificationRepository.save(notification);
    }

    public boolean isOwnerOfDog(String userId, Long dogId) {
        // 특정 userId가 dogId의 소유자인지 확인하는 로직 구현
        return true; // 로직 추가 필요
    }

    public List<Notification> getNotifications(Long dogId) {
        return notificationRepository.findAllByDogIdAndScheduledTimeAfter(dogId, LocalDateTime.now());
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
