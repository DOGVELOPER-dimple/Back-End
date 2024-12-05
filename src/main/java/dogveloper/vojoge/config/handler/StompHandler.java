package dogveloper.vojoge.config.handler;

import dogveloper.vojoge.chat.service.ChatService;
import dogveloper.vojoge.chatRoom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Incoming message: {}", message.toString());
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("DISCONNECT command received, skipping token verification.");
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Processing CONNECT command...");

            return message;
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            try {
                log.info("Processing SEND command...");
                String dogIdHeader = accessor.getFirstNativeHeader("dogId");
                String chatRoomNoHeader = accessor.getFirstNativeHeader("chatRoomNo");

                if (dogIdHeader == null || dogIdHeader.isEmpty()) {
                    log.error("Missing 'dogId' header in SEND message");
                    throw new IllegalArgumentException("Missing 'dogId' header in SEND message");
                }

                if (chatRoomNoHeader == null || chatRoomNoHeader.isEmpty()) {
                    log.error("Missing 'chatRoomNo' header in SEND message");
                    throw new IllegalArgumentException("Missing 'chatRoomNo' header in SEND message");
                }

                Long dogId = Long.valueOf(dogIdHeader);
                Long chatRoomNo = Long.valueOf(chatRoomNoHeader);

                log.info("Valid SEND message with dogId: {} and chatRoomNo: {}", dogId, chatRoomNo);

                // 추가 로직 필요시 처리
            } catch (Exception e) {
                log.error("Error during SEND: {}", e.getMessage(), e);
                throw new MessageDeliveryException(message, "Failed to process SEND message", e);
            }
        }

        return message;
    }


    private void connectToChatRoom(Long chatRoomNo, Long dogId) {
        log.info("Connecting to chatRoomNo: {} with dogId: {}", chatRoomNo, dogId);

        boolean isConnected = chatRoomService.isConnected(chatRoomNo);

        chatRoomService.connectChatRoom(chatRoomNo, dogId);

        chatRoomService.updateUnreadMessagesToRead(chatRoomNo, dogId);

        log.info("Is someone already connected to chatRoomNo {}: {}", chatRoomNo, isConnected);

        if (isConnected) {
            chatService.updateMessage(dogId, chatRoomNo);
        }
    }
}
