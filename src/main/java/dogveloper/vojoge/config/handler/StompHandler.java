package dogveloper.vojoge.config.handler;

import dogveloper.vojoge.chat.service.ChatService;
import dogveloper.vojoge.chatRoom.service.ChatRoomService;
import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.service.DogService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserService userService;
    private final DogService dogService;
    private final JwtTokenProvider jwtTokenProvider;


    @Value("${jwt.secret}")
    private String secretKey;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        log.info("Incoming message: {}", message.toString());
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("현재 명령: {}", accessor.getCommand());
        log.info("현재 메시지 내용: {}", message);

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("DISCONNECT 이벤트...");
            return message;
        }


        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("CONNECT 명령 처리");
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            log.info("SEND 명령 처리");

            return message;
        }

        handleMessage(accessor.getCommand(), accessor);

        log.info("handleMessage 끝");
        return message;
    }
    private void handleMessage(StompCommand stompCommand, StompHeaderAccessor accessor) {
        switch (stompCommand) {
            case CONNECT:
                connectToChatRoom(accessor);
                break;
            case ERROR:
                throw new MessageDeliveryException("error");
            default:
                break;
        }
    }

    private String getAccessToken(StompHeaderAccessor accessor) {

        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        if (authorizationHeader == null) {
            log.info("chat header가 없는 요청입니다.");
            log.info("??");
            throw new MalformedJwtException("jwt");
        }

        String token;
        log.info(authorizationHeader);
        String authorizationHeaderStr = authorizationHeader.replace("[", "").replace("]", "");
        if (authorizationHeaderStr.startsWith("Bearer ")) {
            token = authorizationHeaderStr.replace("Bearer ", "");
        } else {
            log.error("Authorization 헤더 형식이 틀립니다. : {}", authorizationHeader);
            throw new MalformedJwtException("jwt");
        }
        log.info("전처리후 token : {}", token);
        return token;
    }


    private void connectToChatRoom(StompHeaderAccessor accessor) {

        log.info("accessor: " + getAccessToken(accessor));
        String userEmail = verifyAccessToken(getAccessToken(accessor));
        log.info("이메일: " + userEmail);
        User user = userService.findByEmail(userEmail);
        String dogIdHeader = accessor.getFirstNativeHeader("dogId");
        log.info("강아지Id: " + dogIdHeader);
        Long dogId = Long.valueOf(dogIdHeader);
        log.info("강아지Id: " + dogId);
        Dog dog = dogService.findById(dogId);
        log.info("강아지 이름: " + dog.getName());

        Long chatRoomNo = Long.valueOf(getChatRoomNo(accessor));
        log.info("Connecting to chatRoomNo: {} with dogId: {}", chatRoomNo, dogId);

        boolean isConnected = chatRoomService.isConnected(chatRoomNo);

        boolean isAlreadyConnected = chatRoomService.isAlreadyConnected(chatRoomNo, dogId);
        if (isAlreadyConnected) {
            log.warn("중복 연결 시도 감지: chatRoomNo={}, dogId={}", chatRoomNo, dogId);
            return;
        }

        log.info("isConnected 완" + isConnected);
        chatRoomService.connectChatRoom(chatRoomNo, dogId, user);
        log.info("connect 완");
        chatRoomService.updateUnreadMessagesToRead(chatRoomNo, dogId, user);
        log.info("updateUnreadMessageToRead 완");
        log.info("Is someone already connected to chatRoomNo {}: {}", chatRoomNo, isConnected);

        if (isConnected) {
            log.info("updateMesage 입장전");
            chatService.updateMessage(dogId, chatRoomNo);
            log.info("updateMesage 입장완");
        }

    }

    private String verifyAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }

        return jwtTokenProvider.getEmailFromToken(accessToken);
    }

    private Integer getChatRoomNo(StompHeaderAccessor accessor) {
        return Integer.valueOf(Objects.requireNonNull(accessor.getFirstNativeHeader("chatRoomNo")));
    }
}
