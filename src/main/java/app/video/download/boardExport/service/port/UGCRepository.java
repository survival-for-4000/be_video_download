package app.video.download.boardExport.service.port;



import app.video.download.UGC.domain.UGC;
import app.video.download.videoTask.domain.VideoTask;

import java.util.List;

public interface UGCRepository {
    List<UGC> findByVideoTaskIn(List<VideoTask> tasks);

}
