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
    // 현재는 Swagger UI로 리다이렉트하여 테스트하기 쉽게 설정
    private final String FRONTEND_REDIRECT_URL = "http://localhost:8080/swagger-ui/index.html";

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
        try {
            log.info("=== OAuth2 로그인 성공 핸들러 시작 ===");
            log.info("OAuth2 로그인 성공! Authentication: {}", authentication.getName());
            log.info("Authentication Principal: {}", authentication.getPrincipal());
            log.info("Authentication Principal Type: {}", authentication.getPrincipal().getClass().getName());
            log.info("Authentication Authorities: {}", authentication.getAuthorities());

            // CustomOAuth2User 객체 가져오기
            if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
                log.error("Authentication Principal이 CustomOAuth2User가 아닙니다: {}", authentication.getPrincipal().getClass());
                throw new RuntimeException("잘못된 사용자 정보입니다.");
            }

            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            log.info("CustomOAuth2User 정보 - memberId: {}, nickname: {}, profileImageUrl: {}", 
                    oAuth2User.getMemberId(), oAuth2User.getNickname(), oAuth2User.getProfileImageUrl());

            // JWT 토큰 생성 (우리 애플리케이션의 사용자 ID를 사용하여 토큰 생성)
            log.info("JWT 토큰 생성 시작 - memberId: {}", oAuth2User.getMemberId());
            String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getMemberId());
            String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getMemberId());

            log.info("Generated Access Token: {}", accessToken);
            log.info("Generated Refresh Token: {}", refreshToken);

            // TODO: Refresh Token을 DB에 저장하거나 Redis에 저장하는 로직 추가 (보안 강화)

            // 간단한 테스트를 위해 JSON 응답으로 변경
            response.setContentType("application/json;charset=UTF-8");
            String jsonResponse = String.format(
                "{\"success\":true,\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"message\":\"로그인 성공\"}",
                accessToken, refreshToken
            );
            
            log.info("JSON 응답 전송: {}", jsonResponse);
            response.getWriter().write(jsonResponse);
            log.info("=== OAuth2 로그인 성공 핸들러 완료 ===");
            
        } catch (Exception e) {
            log.error("=== OAuth2 로그인 성공 핸들러에서 오류 발생 ===");
            log.error("OAuth2 로그인 성공 핸들러에서 오류 발생", e);
            
            // 오류 응답도 JSON으로 전송
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = String.format(
                "{\"success\":false,\"error\":\"%s\",\"message\":\"로그인 처리 중 오류 발생\"}",
                e.getMessage()
            );
            response.getWriter().write(errorResponse);
        }
    }
}
