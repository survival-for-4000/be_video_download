package app.video.download.boardExport.service.port;

import app.video.download.global.domain.Status;
import app.video.download.videoTask.domain.VideoTask;

import java.util.List;

public interface VideoTaskRepository {

    
    List<VideoTask> findAllByBoardIdAndStatus(Long boardId, Status status);

}
