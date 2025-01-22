package dogveloper.vojoge.notification.controller;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "알림 생성 //준상", description = "새로운 알림을 생성합니다.")
    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request) {
        notificationService.saveNotification(request);
        return ResponseEntity.ok("Notification scheduled successfully");
    }

    @Operation(summary = "실시간 알림 구독 //준상", description = "특정 반려견 ID로 실시간 알림을 구독합니다.")
    @GetMapping(value = "/subscribe/{dogId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long dogId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(dogId, emitter);

        emitter.onCompletion(() -> emitters.remove(dogId));
        emitter.onTimeout(() -> emitters.remove(dogId));

        return emitter;
    }

    @Operation(summary = "구독자 알림 전송 //준상", description = "특정 반려견 ID의 구독자에게 알림을 전송합니다.")
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

    @Operation(summary = "알림 목록 조회 //준상", description = "특정 반려견 ID로 저장된 알림 목록을 조회합니다.")
    @GetMapping("/{dogId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long dogId) {
        List<Notification> notifications = notificationService.getNotifications(dogId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "알림 삭제 //준상", description = "특정 알림 ID를 삭제합니다.")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }
}
