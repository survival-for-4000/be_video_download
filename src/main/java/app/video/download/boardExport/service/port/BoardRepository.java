package app.video.download.boardExport.service.port;


import app.video.download.board.domain.Board;
import java.util.Optional;

public interface BoardRepository {
    Optional<Board> findById(Long id);
}