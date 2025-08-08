package app.video.download.imageTask.domain;

import app.video.download.global.domain.Status;
import app.video.download.global.domain.ResolutionProfile;
import app.video.download.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ImageTask {

    private final Long id;
    private final String prompt;
    private final String checkpoint;
    private final String lora;
    private final String runpodId;
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Member creator;
    private final ResolutionProfile resolutionProfile;

    @Builder
    public ImageTask(Long id, String prompt, String checkpoint, String lora, String runpodId, Status status, LocalDateTime createdAt, LocalDateTime modifiedAt, Member creator, ResolutionProfile resolutionProfile) {
        this.id = id;
        this.prompt = prompt;
        this.checkpoint = checkpoint;
        this.lora = lora;
        this.runpodId = runpodId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.creator = creator;
        this.resolutionProfile = resolutionProfile;
    }

}