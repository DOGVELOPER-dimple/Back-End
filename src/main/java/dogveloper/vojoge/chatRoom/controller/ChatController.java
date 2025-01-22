package dogveloper.vojoge.chatRoom.controller;

import dogveloper.vojoge.chat.dto.ChatRequestDto;
import dogveloper.vojoge.chat.dto.ChattingHistoryResponseDto;
import dogveloper.vojoge.chat.dto.LeaveRequest;
import dogveloper.vojoge.chat.dto.Message;
import dogveloper.vojoge.chat.service.ChatService;
import dogveloper.vojoge.chatRoom.dto.MyChatRoomResponse;
import dogveloper.vojoge.chatRoom.dto.Response;
import dogveloper.vojoge.chatRoom.service.ChatRoomService;
import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.service.DogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final DogService dogService;
    private final ChatService chatService;

    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @PostMapping("/chatroom")
    public ResponseEntity<Response<MyChatRoomResponse>> createChatRoom(@RequestBody ChatRequestDto requestDto){
        //채팅방 생성
        return ResponseEntity.ok(Response.success(chatRoomService.makeChatRoom(requestDto)));
    }

    @Operation(summary = "나의 채팅룸", description = "채팅룸")
    @GetMapping("/my-chatroom/{dogId}")
    public ResponseEntity<Response<List<MyChatRoomResponse>>> chatRoomList(@PathVariable Long dogId){
        List<MyChatRoomResponse> chatRoomList = chatRoomService.getChatRoomList(dogId);

        return ResponseEntity.ok(Response.success(chatRoomList));
    }

    @Operation(summary = "채팅 내역 조회", description = "채팅 내역 조회")
    @GetMapping("/chatroom/{roomNo}/{dogId}")
    public ResponseEntity<Response<ChattingHistoryResponseDto>> chattingList(@PathVariable Long roomNo, @PathVariable Long dogId){
        Dog dog =  dogService.findById(dogId);
        ChattingHistoryResponseDto chattingList = chatService.getChattingList(roomNo, dog.getId());
        return ResponseEntity.ok(Response.success(chattingList));
    }

    @Operation(summary = "채팅 저장과 알람", description = "채팅 저장과 알람")
    @PostMapping("/chatroom/message-alarm-record/{dogId}")
    public ResponseEntity<Response<Message>> sendNotification(@RequestBody Message message, @PathVariable Long dogId){
        Dog dog = dogService.findById(dogId);
        Message savedMessage = chatService.sendAlarmAndSaveMessage(message, dog.getId());
        return ResponseEntity.ok(Response.success(savedMessage));
    }

    @Operation(summary = "채팅방 전송", description = "채팅방 전송")
    @MessageMapping("/message")
    public void sendMessage(Message message, @Header("dogId") Long dogId){
        log.info("보낸 메시지: {}", message.toString());
        log.info("dogId: {}", dogId);
        chatService.sendMessage(message, dogId);
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방 나가기")
    @MessageMapping("/chatroom/leave")
    public void leaveChatRoom(@Payload LeaveRequest leaveRequest, @Header("Authorization") String authorizationHeader){
        Long leaveChatRooomNo = leaveRequest.getChatNo();
        Long dogId = leaveRequest.getDogId();

        chatRoomService.disconnectChatRoom(leaveChatRooomNo, dogId, authorizationHeader);
        chatService.leaveMessage(dogId, leaveChatRooomNo);
    }
}
