package app.video.download.videoTask.infrastructure;

import app.video.download.global.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface VideoTaskJpaRepository extends JpaRepository<VideoTaskEntity, Long>, JpaSpecificationExecutor<VideoTaskEntity> {
    List<VideoTaskEntity> findByBoardIdAndStatusOrderByCreatedAtAsc(Long boardId, Status status);

}