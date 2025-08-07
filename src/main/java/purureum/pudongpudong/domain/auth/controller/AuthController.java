package purureum.pudongpudong.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import purureum.pudongpudong.domain.auth.oauth2.user.CustomOAuth2User;
import purureum.pudongpudong.global.apiPayload.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 컨트롤러
 * OAuth2 로그인 테스트 및 인증 상태 확인을 위한 엔드포인트를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    /**
     * 현재 인증 상태를 확인하는 엔드포인트
     */
    @GetMapping("/status")
    @Operation(summary = "인증 상태 확인", description = "현재 사용자의 인증 상태를 확인합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> status = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            status.put("authenticated", true);
            status.put("username", authentication.getName());
            
            if (authentication.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                status.put("memberId", oAuth2User.getMemberId());
                status.put("email", oAuth2User.getEmail());
                status.put("nickname", oAuth2User.getNickname());
                status.put("provider", "OAuth2");
            }
        } else {
            status.put("authenticated", false);
        }
        
        return ResponseEntity.ok(ApiResponse.onSuccess(status));
    }

    /**
     * OAuth2 로그인 테스트용 엔드포인트
     */
    @GetMapping("/test")
    @Operation(summary = "인증 테스트", description = "인증 API가 정상적으로 작동하는지 테스트합니다.")
    public ResponseEntity<ApiResponse<String>> testAuth() {
        return ResponseEntity.ok(ApiResponse.onSuccess("Auth test endpoint is working!"));
    }
} 