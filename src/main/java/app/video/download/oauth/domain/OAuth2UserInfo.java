package app.video.download.oauth.domain;

import app.video.download.global.error.ErrorCode;
import app.video.download.global.exception.CustomException;
import app.video.download.member.domain.AuthProvider;
import app.video.download.member.domain.Member;
import app.video.download.member.domain.Role;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile,
        String provider,
        String providerId
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            default -> throw new CustomException(ErrorCode.ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider("GOOGLE")
                .providerId(attributes.get("sub").toString())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .provider(AuthProvider.valueOf(provider))
                .providerId(providerId)
                .role(Role.USER)
                .build();
    }
}
