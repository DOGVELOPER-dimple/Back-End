package dogveloper.vojoge.notification.controller;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request) {
        notificationService.saveNotification(request);
        return ResponseEntity.ok("Notification scheduled successfully");
    }

    @GetMapping(value = "/subscribe/{dogId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long dogId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(dogId, emitter);

        emitter.onCompletion(() -> emitters.remove(dogId));
        emitter.onTimeout(() -> emitters.remove(dogId));

        return emitter;
    }

    public void notifySubscribers(Notification notification) {
        SseEmitter emitter = emitters.get(notification.getDogId());
        if (emitter != null) {
            try {
                emitter.send(notification.getMessage());
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(notification.getDogId());
            }
        }
    }

    // 알림 목록 조회 기능
    @GetMapping("/{dogId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long dogId) {
        List<Notification> notifications = notificationService.getNotifications(dogId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 삭제 기능
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }
}
