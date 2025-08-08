package app.video.download.UGC.domain;

import app.video.download.imageTask.domain.ImageTask;
import app.video.download.member.domain.Member;
import app.video.download.videoTask.domain.VideoTask;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UGC {

    private Long id;
    private String url;
    private int index;
    private ImageTask imageTask;
    private VideoTask videoTask;
    private LocalDateTime createdAt;
    private Member creator;

    @Builder
    public UGC(Long id, String url, int index, ImageTask imageTask, VideoTask videoTask, LocalDateTime createdAt, Member creator){
        this.id = id;
        this.url = url;
        this.index = index;
        this.imageTask = imageTask;
        this.videoTask = videoTask;
        this.createdAt = createdAt;
        this.creator = creator;
    }
}