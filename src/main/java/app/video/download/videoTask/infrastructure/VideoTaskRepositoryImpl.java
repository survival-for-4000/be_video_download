package app.video.download.videoTask.infrastructure;


import app.video.download.boardExport.service.port.VideoTaskRepository;
import app.video.download.global.domain.Status;
import app.video.download.member.domain.Member;
import app.video.download.member.infrastructure.MemberEntity;
import app.video.download.videoTask.domain.VideoTask;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VideoTaskRepositoryImpl  implements VideoTaskRepository {

    private final VideoTaskJpaRepository jpaRepository;

    @Override
    public List<VideoTask> findAllByBoardIdAndStatus(Long boardId, Status status) {
        return jpaRepository.findByBoardIdAndStatusOrderByCreatedAtAsc(boardId, status)
                .stream()
                .map(VideoTaskEntity::toModel)
                .collect(Collectors.toList());
    }

}
