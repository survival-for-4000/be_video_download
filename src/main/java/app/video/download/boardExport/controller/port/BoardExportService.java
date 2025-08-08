package app.video.download.boardExport.controller.port;



import app.video.download.boardExport.controller.request.BoardExportRequest;
import app.video.download.boardExport.controller.response.BoardExportResponse;
import app.video.download.boardExport.domain.BoardExport;
import app.video.download.member.domain.Member;

import java.util.List;

public interface BoardExportService {
    BoardExportResponse exportBoardVideos(Long boardId, BoardExportRequest request, Member member);
    List<BoardExport> getExportHistory(Long boardId);
}