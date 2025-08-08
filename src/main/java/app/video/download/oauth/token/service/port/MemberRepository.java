package app.video.download.oauth.token.service.port;


import app.video.download.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByEmail(String email);

    Member save(Member newMember);

}
