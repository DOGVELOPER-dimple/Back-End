package dogveloper.vojoge.chat.kafka;

import dogveloper.vojoge.chat.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReceiver {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(Message message) {
        messagingTemplate.convertAndSend("/subscribe/" + message.getChatNo(), message);
    }
}
