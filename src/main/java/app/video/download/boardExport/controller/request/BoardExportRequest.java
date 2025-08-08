package app.video.download.boardExport.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardExportRequest {
    private ExportSettings exportSettings;
    
    @Getter
    @NoArgsConstructor
    public static class ExportSettings {
        private String format = "mp4";
        private String quality = "medium"; // "high", "medium", "low"
        private String resolution; // optional, auto-detect from videos
        private boolean includeTransitions = false; // future feature
    }
}