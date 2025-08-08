package app.video.download.UGC.infrastructure;


import app.video.download.UGC.domain.UGC;
import app.video.download.boardExport.service.port.UGCRepository;
import app.video.download.videoTask.domain.VideoTask;
import app.video.download.videoTask.infrastructure.VideoTaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UGCRepositoryImpl implements UGCRepository {

    private final UGCJpaRepository ugcJpaRepository;

    @Override
    public List<UGC> findByVideoTaskIn(List<VideoTask> tasks) {
        List<VideoTaskEntity> entities = tasks.stream()
         //       .map(t -> videoTaskJpaRepository.getReferenceById(t.getId()))
               .map(VideoTaskEntity::from) //이렇게 해도 됨.. 왠지는 알아볼 것
                .toList();

        return ugcJpaRepository.findByVideoTaskIn(entities)
                .stream()
                .map(UGCEntity::toModel)
                .toList();
    }

}

