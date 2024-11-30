package dogveloper.vojoge.chat.service;


import dogveloper.vojoge.alarm.domain.Alarm;
import dogveloper.vojoge.alarm.repository.AlarmRepository;
import dogveloper.vojoge.chat.domain.Chat;
import dogveloper.vojoge.chat.dto.ChatResponseDto;
import dogveloper.vojoge.chat.dto.ChattingHistoryResponseDto;
import dogveloper.vojoge.chat.dto.Message;
import dogveloper.vojoge.chat.kafka.MessageSender;
import dogveloper.vojoge.chat.mongo.Chatting;
import dogveloper.vojoge.chat.mongo.MongoChatRepository;
import dogveloper.vojoge.chat.repository.ChatRepository;
import dogveloper.vojoge.chatRoom.service.ChatRoomService;
import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static dogveloper.vojoge.alarm.enums.AlarmType.NEW_CHAT;
import static dogveloper.vojoge.chat.kafka.KafkaConstants.KAFKA_TOPIC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final MongoChatRepository mongoChatRepository;
    private final MessageSender sender;
    private final ChatRepository chatRepository;
    private final AlarmRepository alarmRepository;
    private final DogRepository dogRepository;
    private final ChatRoomService chatRoomService;

    public ChattingHistoryResponseDto getChattingList(Long chatRoomNo, Long dogId){
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID"));
        List<ChatResponseDto> chattingList = mongoChatRepository.findByChatRoomNo(chatRoomNo).stream()
                .map(chat -> new ChatResponseDto(chat, dog.getId(), dog.getName()))
                .collect(Collectors.toList());
        return ChattingHistoryResponseDto.builder()
                .dogName(dog.getName())
                .chatList(chattingList)
                .build();
    }

    public void sendMessage(Message message, Long id){
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID"));

        boolean isRead = chatRoomService.isAllConnected(message.getChatNo());

        message.setSendTimeAndSender(LocalDateTime.now(), dog.getName(), isRead);

        sender.send(KAFKA_TOPIC, message);

        Chatting chatting = message.toEntity(id);
        Chatting savedChat = mongoChatRepository.save(chatting);
        message.setId(savedChat.getId());
    }

    @Transactional
    public Message sendAlarmAndSaveMessage(Message message, Long dogId){
        Dog sender = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID"));

        if(message.isRead()){
            Chat findChat = chatRepository.findById(message.getChatNo()).orElseThrow();

            Dog recipient = findChat.getJoinDog();
            alarmRepository.save(Alarm.builder().dog(recipient)
                    .alarmType(NEW_CHAT)
                    .text(NEW_CHAT.getAlarmText())
                    .targetId(recipient.getId())
                            .fromDogId(sender.getId())
                            .dog(sender)
                    .build());
        }

        Chatting chatting = message.toEntity(dogId);
        Chatting savedChat = mongoChatRepository.save(chatting);
        message.setId(savedChat.getId());

        return message;
    }

    public void updateMessage(Long dogId, Long chatRoomNo) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID"));
        Message message = Message.builder()
                .chatNo(chatRoomNo)
                .content(dog.getName() + "님이 입장했습니다.")
                .senderName(dog.getName())
                .build();
        sender.send(KAFKA_TOPIC, message);
    }

    public void leaveMessage(Long dogId, Long chatRoomNo) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID"));
        Message message = Message.builder()
                .chatNo(chatRoomNo)
                .content(dog.getName() + "님이 방을 나갔습니다.")
                .senderName(dog.getName())
                .build();

        sender.send(KAFKA_TOPIC, message);
    }
}
