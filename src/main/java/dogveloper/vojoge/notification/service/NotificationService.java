package dogveloper.vojoge.notification.service;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<Notification> getNotifications(Long dogId) {
        return notificationRepository.findAllByDogId(dogId);
    }
    public Notification updateNotification(Long notificationId, NotificationRequest request){
        return notificationRepository.findById(notificationId).map(notification -> {
            notification.setMessage(request.getMessage());
            notification.setScheduledTime(request.getScheduledTime());
            notification.setSent(request.isSent());
            return notificationRepository.save(notification);
        }).orElse(null);
    }
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
