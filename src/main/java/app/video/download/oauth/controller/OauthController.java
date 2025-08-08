package app.video.download.oauth.controller;


import app.video.download.global.dto.GlobalResponse;
import app.video.download.global.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OauthController {

    @PostMapping("/logout")
    public GlobalResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {

        CookieUtils.deleteCookie(request, response, "_hoauth");
        CookieUtils.deleteCookie(request, response, "_hrauth");

        return GlobalResponse.success("로그아웃 되었습니다.");
    }
}