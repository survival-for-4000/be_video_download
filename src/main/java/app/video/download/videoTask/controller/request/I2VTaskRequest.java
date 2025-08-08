package app.video.download.videoTask.controller.request;

import app.video.download.global.domain.ResolutionProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class I2VTaskRequest {
    private String prompt;
    private String imageUrl;
    private ResolutionProfile resolutionProfile;
    private int numFrames;
}