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
import dogveloper.vojoge.dog.service.DogService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final DogService dogService;
    private final ChatRepository chatRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MyChatRoomResponse makeChatRoom(ChatRequestDto requestDto){
        Dog findDog = dogService.findById(requestDto.getSeletedDogId());
        Dog joinDog = dogService.findById(requestDto.getJoinDogId());
        log.info("채팅방 만들기");
        if(!dogService.validation(joinDog)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }

        chatRepository.findActiveChat(findDog, joinDog)
                .ifPresent(chat ->{
                    throw new IllegalStateException("이미 활성화된 채팅방이 존재합니다.");
                });

        Chat chat = Chat.builder()
                .createDog(findDog)
                .joinDog(joinDog)
                .localDateTime(LocalDateTime.now())
                .build();
        chatRepository.save(chat);
        return chat.toResponse(joinDog,0L, null);
    }

    public List<MyChatRoomResponse> getChatRoomList(Long dogId) {

        Dog findDog = dogService.findById(dogId);

        if(!dogService.validation(findDog)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }


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
    public void connectChatRoom(Long chatRoomNo, Long dogId, User user) {

        Dog dog = dogService.findById(dogId);
        log.info("강아지"+dog.getName());
        if(!dog.getUser().equals(user)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }
        log.info("강아지회원 정보 일치");

        Chat chat = chatRepository.findById(chatRoomNo)
                .orElseThrow(()-> new EntityNotFoundException("존재 하지 않는 챗팅방"));

        if(!chat.getCreateDog().equals(dog) && !chat.getJoinDog().equals(dog))
        {
            throw new IllegalStateException("사용자는 채팅방에 존재하지 않는 회원입니다.");
        }

        if (isAlreadyConnected(chatRoomNo, dogId)) {
            throw new IllegalStateException("이미 채팅방에 연결된 사용자입니다.");

        }

        ChatRoom chatRoom = ChatRoom.builder()
                .dogId(dogId)
                .chatroomNo(chatRoomNo)
                .build();

        log.info("Saving to Redis: chatRoomNo={}, dogId={}", chatRoomNo, dogId);

        redisChatRoomRepository.save(chatRoom);

        log.info("Saved to Redis successfully");
    }

    @Transactional
    public void disconnectChatRoom(Long chatRoomNo, Long dogId, String authorizationHeader){
        log.info("접속 종료 진입");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 인증 정보입니다.");
        }
        String token = authorizationHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalStateException("토큰이 유효하지 않습니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.findByEmail(email);
        log.info("인증된 사용자: {}", user.getEmail());

        Dog dog = dogService.findById(dogId);
        if(!dog.getUser().equals(user)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }

        ChatRoom chatRoom = redisChatRoomRepository.findByChatroomNoAndDogId(chatRoomNo, dogId)
                .orElseThrow(IllegalArgumentException::new);
        log.info(chatRoom.toString());
        redisChatRoomRepository.delete(chatRoom);
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        log.info("참여자 수: " + connectedList.size());
    }

    public boolean isConnected(Long chatRoomNo){
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        log.info("참여자 수: " + connectedList.size());
        return connectedList.size() == 1;
    }

    public boolean isAllConnected(Long chatRoomNo){
        List<ChatRoom> connectedList = redisChatRoomRepository.findByChatroomNo(chatRoomNo);
        log.info("참여자 수: " + connectedList.size());
        return connectedList.size() == 2;
    }

    public Long countUnReadMessages(Long chatRoomNo, Long senderId){
        Dog dog = dogService.findById(senderId);
        if(!dogService.validation(dog)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }
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

    public void updateUnreadMessagesToRead(Long chatRoomNo, Long dogId, User user){
        Dog dog = dogService.findById(dogId);
        if(!dog.getUser().equals(user)){
            throw new IllegalStateException("회원 정보와 강아지 정보가 일치하지 않습니다.");
        }

        Update update = new Update().set("isRead", true);
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoomNo).and("senderId").ne(dog.getId()));

        mongoTemplate.updateMulti(query, update, Chatting.class);
    }

    public boolean isAlreadyConnected(Long chatRoomNo, Long dogId) {
        return redisChatRoomRepository.findByChatroomNoAndDogId(chatRoomNo, dogId).isPresent();
    }
}
