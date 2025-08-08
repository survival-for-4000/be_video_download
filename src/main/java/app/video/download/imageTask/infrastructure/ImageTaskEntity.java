package app.video.download.imageTask.infrastructure;

import app.video.download.global.domain.ResolutionProfile;
import app.video.download.global.domain.Status;
import app.video.download.imageTask.domain.ImageTask;
import app.video.download.member.infrastructure.MemberEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "image_tasks")
public class ImageTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    private String checkpoint;

    private String lora;

    private String runpodId;

    @Enumerated(EnumType.STRING)
    private ResolutionProfile resolutionProfile;

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

    public static ImageTaskEntity from(ImageTask task) {
        ImageTaskEntity taskEntity = new ImageTaskEntity();
        taskEntity.id = task.getId();
        taskEntity.checkpoint = task.getCheckpoint();
        taskEntity.lora = task.getLora();
        taskEntity.prompt = task.getPrompt();
        taskEntity.runpodId = task.getRunpodId();
        taskEntity.resolutionProfile = task.getResolutionProfile();
        taskEntity.status = task.getStatus();
        taskEntity.createdAt = task.getCreatedAt();
        taskEntity.modifiedAt = task.getModifiedAt();
        taskEntity.member = MemberEntity.from(task.getCreator());

        return taskEntity;
    }

    public ImageTask toModel() {
        return ImageTask.builder()
                .id(id)
                .prompt(prompt)
                .checkpoint(checkpoint)
                .lora(lora)
                .runpodId(runpodId)
                .resolutionProfile(resolutionProfile)
                .status(status)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .creator(member.toModel())
                .build();
    }
}