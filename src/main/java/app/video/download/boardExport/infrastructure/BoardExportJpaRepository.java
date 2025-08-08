package app.video.download.boardExport.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardExportJpaRepository extends JpaRepository<BoardExportEntity, Long> {
    List<BoardExportEntity> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}