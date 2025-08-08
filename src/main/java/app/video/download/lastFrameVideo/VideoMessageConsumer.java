package app.video.download.lastFrameVideo;

import app.video.download.config.RabbitMQConfig;
import app.video.download.global.port.S3Service;
import app.video.download.global.port.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoMessageConsumer {

    private final VideoProcessingService processingService;
    private final S3Service s3Service;
    private final VideoMessageProducer messageProducer;

    @RabbitListener(queues = RabbitMQConfig.DOWNLOAD_QUEUE)
    public void processCreation(I2VQueueRequest message) {
        try {
            MultipartFile file = processingService.extractLatestFrameFromVideo(message.getUrl());
            String url = s3Service.uploadFile(file);

            I2VQueueRequest result = new I2VQueueRequest(
                message.getTaskId(), 
                message.getPrompt(), 
                url, 
                message.getWidth(),
                message.getHeight(),
                message.getNumFrames(),
                message.getMemberId()
            );
            messageProducer.sendCreationMessage(result);
            log.info("영상 생성 요청 처리 완료");
        } catch (Exception e) {
            log.error("영상 생성 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

}
