package app.video.download.member.infrastructure;

import app.video.download.member.domain.Member;
import app.video.download.oauth.token.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).map(MemberEntity::toModel);
    }

    @Override
    public Member save(Member member){
        return memberJpaRepository.save(MemberEntity.from(member)).toModel();
    }

}
