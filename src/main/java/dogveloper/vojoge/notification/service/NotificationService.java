package dogveloper.vojoge.notification.service;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification saveNotification(NotificationRequest request, Long userId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .dogId(request.getDogId())
                .message(request.getMessage())
                .scheduledTime(request.getScheduledTime())
                .sent(false)
                .build();
        return notificationRepository.save(notification);
    }
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }
    public List<Notification> getNotificationsByDog(Long userId, Long dogId){
        return notificationRepository.findAllByUserIdAndDogId(userId, dogId);
    }
    public Notification updateNotification(Long notificationId, NotificationRequest request){
        return notificationRepository.findById(notificationId).map(notification -> {
            Optional.ofNullable(request.getMessage()).ifPresent(notification::setMessage);
            Optional.ofNullable(request.getScheduledTime()).ifPresent(notification::setScheduledTime);
            Optional.ofNullable(request.isSent()).ifPresent(notification::setSent);
            return notificationRepository.save(notification);
        }).orElse(null);
    }
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
