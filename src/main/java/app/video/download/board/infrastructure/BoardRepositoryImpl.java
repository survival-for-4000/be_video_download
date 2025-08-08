package app.video.download.board.infrastructure;

import app.video.download.board.domain.Board;
import app.video.download.boardExport.service.port.BoardRepository;
import app.video.download.member.domain.Member;
import app.video.download.member.infrastructure.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;

    @Override
    public Optional<Board> findById(Long id) {
        return boardJpaRepository.findById(id)
                .map(BoardEntity::toModel);
    }

}