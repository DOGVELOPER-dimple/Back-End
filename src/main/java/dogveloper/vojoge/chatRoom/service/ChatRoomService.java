package dogveloper.vojoge.chatRoom.service;

import dogveloper.vojoge.chat.domain.Chat;
import dogveloper.vojoge.chat.dto.ChatRequestDto;
import dogveloper.vojoge.chat.mongo.Chatting;
import dogveloper.vojoge.chat.repository.ChatRepository;
import dogveloper.vojoge.chatRoom.domain.ChatRoom;
import dogveloper.vojoge.chatRoom.dto.MyChatRoomResponse;
import dogveloper.vojoge.chatRoom.repository.RedisChatRoomRepository;
import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.repository.DogRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final DogRepository dogRepository;
    private final ChatRepository chatRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Chat makeChatRoom(Long dogId, ChatRequestDto requestDto){
        Dog findDog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID: " + dogId));
        Dog joinDog = dogRepository.findById(requestDto.getJoinDogId())
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID: " + dogId));

        chatRepository.findActiveChat(findDog, joinDog)
                .ifPresent(chat ->{
                    throw new IllegalStateException("이미 활성화된 채팅방이 존재합니다.");
                });
        Chat chat = Chat.builder()
                .createDog(findDog)
                .joinDog(joinDog)
                .localDateTime(LocalDateTime.now())
                .build();
        return chatRepository.save(chat);
    }

    public List<MyChatRoomResponse> getChatRoomList(Long dogId) {

        Dog findDog = dogRepository.findById(dogId).orElseThrow();


        return chatRepository.findAllByCreateDogOrJoinDog(findDog,findDog).stream().map(chat -> {
            Dog dog;
            if (!Objects.equals(findDog.getId(), chat.getCreateDog())) {

                dog = chat.getCreateDog();
            } else {
                dog = chat.getJoinDog();
            }

            Long unReadMessages = countUnReadMessages(chat.getId(), dogId);
            String lastMessage = findLastMessage(chat.getId());

            return chat.toResponse(dog, unReadMessages, lastMessage);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void connectChatRoom(Long chatRoomNo, Long dogId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .dogId(dogId)
                .chatroomNo(chatRoomNo)
                .build();

        log.info("Saving to Redis: chatRoomNo={}, dogId={}", chatRoomNo, dogId);

        redisChatRoomRepository.save(chatRoom);

        log.info("Saved to Redis successfully");
    }

    @Transactional
    public void disconnectChatRoom(Long chatRoomNo, Long dogId){
        ChatRoom chatRoom = redisChatRoomRepository.findByChatroomNoAndDogId(chatRoomNo, dogId)
                .orElseThrow(IllegalArgumentException::new);

        redisChatRoomRepository.delete(chatRoom);
    }

    public boolean isConnected(Long chatRoomNo){
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 1;
    }

    public boolean isAllConnected(Long chatRoomNo){
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        return connectedList.size() == 2;
    }

    public Long countUnReadMessages(Long chatRoomNo, Long senderId){
        Query query = new Query(
                Criteria.where("chatRoomNo").is(chatRoomNo)
                        .and("isRead").is(false)
                        .and("senderId").ne(senderId)
        );
        return mongoTemplate.count(query, Chatting.class);
    }

    public String findLastMessage(Long chatRoomNo){
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo))
                .with(Sort.by(Sort.Order.desc("sendDate")))
                .limit(1);

        try {
            return mongoTemplate.findOne(query, Chatting.class).getContent();
        }catch (Exception e){
            log.info(e.getMessage());
            return "";
        }
    }

    public void updateUnreadMessagesToRead(Long chatRoomNo, Long dogId){
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with ID: " + dogId));

        Update update = new Update().set("isRead", true);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("senderId").ne(dog.getId()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }
}
