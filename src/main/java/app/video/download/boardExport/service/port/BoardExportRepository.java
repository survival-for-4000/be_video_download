package app.video.download.boardExport.service.port;


import app.video.download.boardExport.domain.BoardExport;

import java.util.List;

public interface BoardExportRepository {
    BoardExport save(BoardExport boardExport);
    List<BoardExport> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}