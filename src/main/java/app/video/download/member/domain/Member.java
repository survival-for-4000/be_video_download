package app.video.download.member.domain;

import app.video.download.global.error.ErrorCode;
import app.video.download.global.exception.CustomException;
import lombok.*;

@Getter
public class Member  {
    private Long id;
    private String name;
    private String email;
    private String profile;
    private int credit;
    private Role role;
    private AuthProvider provider;
    private String providerId;

    @Builder
    public Member(Long id, String name, String email, String profile, int credit, Role role, AuthProvider provider, String providerId){
        this.id = id;
        this.name = name;
        this.email =email;
        this.profile = profile;
        this.credit = credit;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

}