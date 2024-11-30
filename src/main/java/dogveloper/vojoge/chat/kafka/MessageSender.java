package dogveloper.vojoge.chat.kafka;

import dogveloper.vojoge.chat.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public void send(String topic, Message data) {
        kafkaTemplate.send(topic, data);
    }
}
