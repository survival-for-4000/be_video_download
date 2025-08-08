package app.video.download.oauth.token.controller;


import app.video.download.global.dto.GlobalResponse;
import app.video.download.oauth.token.controller.request.AccessTokenRequest;
import app.video.download.oauth.token.service.TokenGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static app.video.download.global.util.CookieUtils.addCookie;


@RequiredArgsConstructor
@RestController
public class TokenController {

    private final TokenGenerator tokenGenerator;

    @PostMapping("/auth/token/verify")
    public ResponseEntity<GlobalResponse<String>> getToken(@RequestBody AccessTokenRequest request , HttpServletResponse response) {
        String accessToken = tokenGenerator.generateAccessTokenFromRefreshToken(request.get_hrauth());

        addCookie(response, "_hoauth", accessToken, 3600);

        return ResponseEntity.ok()
                .body(GlobalResponse.success("재인증되었습니다."));

    }
}


