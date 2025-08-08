package app.video.download.videoTask.infrastructure;

import app.video.download.board.infrastructure.BoardEntity;
import app.video.download.global.domain.ResolutionProfile;
import app.video.download.global.domain.Status;
import app.video.download.member.infrastructure.MemberEntity;
import app.video.download.videoTask.domain.VideoTask;
import app.video.download.weight.infrasturcture.WeightEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "video_tasks")
@EntityListeners(AuditingEntityListener.class)
public class VideoTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @ManyToOne
    @JoinColumn(name = "lora_id", nullable = true)
    private WeightEntity lora;

    @Column(nullable = true)
    private String url;

    private String runpodId;

    @Enumerated(EnumType.STRING)
    private ResolutionProfile resolutionProfile;

    private int numFrames;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(name= "created_at")
    private LocalDateTime createdAt;

    @CreatedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = true)
    private BoardEntity board;

    public static VideoTaskEntity from(VideoTask task) {
        VideoTaskEntity taskEntity = new VideoTaskEntity();
        taskEntity.id = task.getId();
        taskEntity.prompt = task.getPrompt();
        taskEntity.lora = task.getLora() != null ? WeightEntity.from(task.getLora()) : null;
        taskEntity.url = task.getImageUrl();
        taskEntity.runpodId = task.getRunpodId();
        taskEntity.createdAt = task.getCreatedAt();
        taskEntity.modifiedAt = task.getModifiedAt();
        taskEntity.status = task.getStatus();
        taskEntity.member = MemberEntity.from(task.getCreator());
        taskEntity.resolutionProfile= task.getResolutionProfile();
        taskEntity.numFrames = task.getNumFrames();
        taskEntity.board = task.getBoard() != null ? BoardEntity.from(task.getBoard()) : null;

        return taskEntity;
    }

    public VideoTask toModel() {
        return VideoTask.builder()
                .id(id)
                .prompt(prompt)
                .lora(lora != null ? lora.toModel() : null)
                .imageUrl(url)
                .runpodId(runpodId)
                .status(status)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .creator(member.toModel())
                .resolutionProfile(resolutionProfile)
                .numFrames(numFrames)
                .board(board != null ? board.toModel() : null)
                .build();
    }
}