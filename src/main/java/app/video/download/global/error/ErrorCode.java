package app.video.download.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "제공된 입력 값이 유효하지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 요청 방식입니다."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청한 엔티티를 찾을 수 없습니다."),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "필수 입력 값이 누락되었습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // JWT Token
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    //크레딧
    NO_CREDIT(HttpStatus.PAYMENT_REQUIRED, "크레딧이 부족합니다."),



    // User 관련 에러
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "인증되지 않은 유저입니다."),
    SC_FORBIDDEN(HttpStatus.UNAUTHORIZED, "권한이 없는 유저입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "서명 검증에 실패했습니다." ),
    ILLEGAL_REGISTRATION_ID(HttpStatus.BAD_REQUEST,"해당 사항이 없는 로그인 경로입니다."),


    BAD_REQUEST(HttpStatus.BAD_REQUEST, "적절하지 않은 요청입니다."),

    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "적절하지 않은 JSON 형식입니다."),

    //결제 실패
    PAYMENT_CONFIRMATION_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),

    ERROR_WHILE_UPLOADING(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 도중 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
