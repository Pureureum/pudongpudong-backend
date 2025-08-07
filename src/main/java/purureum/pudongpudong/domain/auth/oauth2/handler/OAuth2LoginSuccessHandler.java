package purureum.pudongpudong.domain.auth.oauth2.handler;

import purureum.pudongpudong.domain.auth.oauth2.user.CustomOAuth2User;
import purureum.pudongpudong.global.jwt.JwtTokenProvider; // JWT 토큰 생성을 위한 클래스 (아직 구현 안 됨)
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 로그인 성공 시 호출되는 핸들러.
 * JWT 토큰을 생성하고, 프론트엔드 URL로 리다이렉트하여 토큰을 전달합니다.
 */
@Slf4j // 로깅을 위한 Lombok 어노테이션
@Component // Spring 빈으로 등록
@RequiredArgsConstructor // final 필드를 주입받기 위한 Lombok 어노테이션
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성 및 관리를 위한 서비스

    // TODO: 프론트엔드 리다이렉트 URL 설정 (application.yml 등에서 관리하는 것이 좋습니다)
    // 현재는 예시 URL을 사용합니다.
    private final String FRONTEND_REDIRECT_URL = "http://localhost:3000/oauth2/redirect";

    /**
     * OAuth2 로그인 성공 시 처리 로직.
     *
     * @param request        HttpServletRequest 객체
     * @param response       HttpServletResponse 객체
     * @param authentication Authentication 객체 (로그인한 사용자 정보 포함)
     * @throws IOException      입출력 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공! Authentication: {}", authentication.getName());

        // CustomOAuth2User 객체 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // JWT 토큰 생성 (우리 애플리케이션의 사용자 ID를 사용하여 토큰 생성)
        // TODO: roles 정보도 토큰에 포함시키는 로직 추가 고려
        String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getMemberId(), oAuth2User.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getMemberId(), oAuth2User.getEmail());

        log.info("Generated Access Token: {}", accessToken);
        log.info("Generated Refresh Token: {}", refreshToken);

        // TODO: Refresh Token을 DB에 저장하거나 Redis에 저장하는 로직 추가 (보안 강화)

        // 프론트엔드 URL로 리다이렉트 (JWT 토큰을 쿼리 파라미터로 전달)
        // 실제 운영 환경에서는 토큰을 HTTP Only 쿠키나 다른 보안적인 방법으로 전달하는 것을 고려해야 합니다.
        String redirectUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URL)
                .queryParam("accessToken", URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
                .queryParam("refreshToken", URLEncoder.encode(refreshToken, StandardCharsets.UTF_8))
                .build().toUriString();

        log.info("Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
