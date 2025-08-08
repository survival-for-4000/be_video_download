package app.video.download.UGC.infrastructure;



import app.video.download.videoTask.infrastructure.VideoTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UGCJpaRepository extends JpaRepository<UGCEntity, Long>, JpaSpecificationExecutor<UGCEntity> {

    @Query("SELECT u FROM UGCEntity u WHERE u.videoTask IN :tasks")
    List<UGCEntity> findByVideoTaskIn(@Param("tasks") List<VideoTaskEntity> tasks);


}
