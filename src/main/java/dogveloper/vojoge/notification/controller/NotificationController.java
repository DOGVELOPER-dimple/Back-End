package dogveloper.vojoge.notification.controller;

import dogveloper.vojoge.notification.domain.Notification;
import dogveloper.vojoge.notification.dto.NotificationRequest;
import dogveloper.vojoge.notification.service.NotificationService;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Operation(summary = "알림 생성 //준상", description = "새로운 알림을 생성합니다.", security = @SecurityRequirement(name = "bearer Auth"))
    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request) {
        User user = userService.getAuthenticatedUser();
        notificationService.saveNotification(request, user.getId());
        return ResponseEntity.ok("Notification scheduled successfully");
    }

    @Operation(summary = "실시간 알림 구독", description = "사용자가 알림을 허용한 경우에만 SSE 구독 가능")
    @GetMapping(value = "/subscribe/{dogId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable Long dogId) {
        User user = userService.getAuthenticatedUser();

        if (!user.isAllowNotifications()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 알림 허용 안 했으면 구독 X
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(dogId, emitter);

        emitter.onCompletion(() -> emitters.remove(dogId));
        emitter.onTimeout(() -> emitters.remove(dogId));

        return ResponseEntity.ok(emitter);
    }

    @Operation(summary = "구독자 알림 전송 //준상", description = "특정 반려견 ID의 구독자에게 알림을 전송합니다.", security = @SecurityRequirement(name = "bearer Auth"))
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

    @Operation(summary = "유저의 모든 알림 조회 //준상", description = "유저 ID로 모든 알림을 조회합니다.", security = @SecurityRequirement(name = "bearer Auth"))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId){
        List<Notification> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "강아지의 알림 조회 //준상", description = "유저 ID와 강아지 ID로 알림을 조회합니다.", security = @SecurityRequirement(name = "bearer Auth"))
    @GetMapping("/user/{userId}/dog/{dogId}")
    public ResponseEntity<List<Notification>> getNotificationsByDog(
            @PathVariable Long userId,
            @PathVariable Long dogId){
        List<Notification> notifications = notificationService.getNotificationsByDog(userId, dogId);
        return ResponseEntity.ok(notifications);
    }


    @Operation(summary = "알림 수정 //준상", description = "기존 알림을 수정합니다.", security = @SecurityRequirement(name = "bearer Auth"))
    @PutMapping("/{notificationId}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long notificationId,
            @RequestBody NotificationRequest request){

        Notification updatedNotification = notificationService.updateNotification(notificationId, request);
        if (updatedNotification != null){
            return ResponseEntity.ok(updatedNotification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "알림 삭제 //준상", description = "특정 알림 ID를 삭제합니다.", security = @SecurityRequirement(name = "bearer Auth"))
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }
}
