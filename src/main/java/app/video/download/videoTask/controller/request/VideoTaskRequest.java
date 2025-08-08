package app.video.download.videoTask.controller.request;

import app.video.download.global.domain.ResolutionProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoTaskRequest {
    private Long loraId = 1L;;

    private String prompt;

    private ResolutionProfile resolutionProfile;

    private int numFrames;
}