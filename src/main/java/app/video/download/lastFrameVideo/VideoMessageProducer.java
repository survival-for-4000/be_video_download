package app.video.download.lastFrameVideo;

import app.video.download.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendCreationMessage(I2VQueueRequest message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.I2V_EXCHANGE,
                RabbitMQConfig.I2V_ROUTING_KEY,
                message
        );
    }
}
