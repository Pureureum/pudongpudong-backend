package purureum.pudongpudong.domain.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 로그인 실패 시 호출되는 핸들러.
 */
@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 로그인 실패!", exception);
        log.error("실패 원인: {}", exception.getMessage());
        log.error("요청 URL: {}", request.getRequestURL());
        log.error("쿼리 파라미터: {}", request.getQueryString());
        
        // URL 인코딩 문제를 해결하기 위해 간단한 오류 메시지만 전달
        String errorMessage = "OAuth2 로그인 실패";
        if (exception.getMessage() != null && exception.getMessage().contains("401")) {
            errorMessage = "카카오 인증 실패 - 클라이언트 ID 또는 시크릿을 확인해주세요";
        }
        
        // 실패 시 로그인 페이지로 리다이렉트 (URL 인코딩 문제 해결)
        String redirectUrl = "/login?error=oauth2_failed&message=" + 
                           URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}
