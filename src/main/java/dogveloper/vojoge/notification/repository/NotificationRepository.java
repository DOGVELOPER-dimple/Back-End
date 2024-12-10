package dogveloper.vojoge.notification.repository;

import dogveloper.vojoge.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByDogIdAndScheduledTimeAfter(Long dogId, LocalDateTime now);

    List<Notification> findByScheduledTimeBeforeAndSent(LocalDateTime now, boolean sent);
}
