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
import org.springframework.web.bind.annotation.RequestParam;
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
     * 카카오 설정 확인 엔드포인트
     */
    @GetMapping("/kakao/config")
    @Operation(summary = "카카오 설정 확인", description = "카카오 OAuth2 설정을 확인합니다.")
    public ResponseEntity<ApiResponse<Map<String, String>>> kakaoConfig() {
        log.info("카카오 설정 확인 요청 받음");
        Map<String, String> config = new HashMap<>();
        config.put("clientId", System.getenv("KAKAO_CLIENT_ID") != null ? 
                   System.getenv("KAKAO_CLIENT_ID") : "환경변수에서 찾을 수 없음");
        config.put("clientSecret", System.getenv("KAKAO_CLIENT_SECRET") != null ? 
                   "설정됨" : "환경변수에서 찾을 수 없음");
        config.put("redirectUri", "http://localhost:8080/login/oauth2/code/kakao");
        config.put("authorizationUri", "https://kauth.kakao.com/oauth/authorize");
        config.put("tokenUri", "https://kauth.kakao.com/oauth/token");
        config.put("userInfoUri", "https://kapi.kakao.com/v2/user/me");
        
        return ResponseEntity.ok(ApiResponse.onSuccess(config));
    }

    /**
     * 카카오 OAuth2 로그인 시작 엔드포인트
     */
    @GetMapping("/kakao/login")
    @Operation(summary = "카카오 로그인", description = "카카오 OAuth2 로그인을 시작합니다.")
    public ResponseEntity<ApiResponse<Map<String, String>>> kakaoLogin() {
        log.info("카카오 로그인 요청 받음");
        Map<String, String> response = new HashMap<>();
        response.put("message", "카카오 로그인을 시작합니다.");
        response.put("loginUrl", "/oauth2/authorization/kakao");
        response.put("description", "브라우저에서 /oauth2/authorization/kakao로 이동하여 카카오 로그인을 진행하세요.");
        
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /**
     * 카카오 로그인 콜백 처리 엔드포인트
     */
    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 OAuth2 로그인 콜백을 처리합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kakaoCallback(@RequestParam(required = false) String code, 
                                                                         @RequestParam(required = false) String error) {
        log.info("카카오 콜백 요청 받음 - code: {}, error: {}", code, error);
        Map<String, Object> response = new HashMap<>();
        
        if (error != null) {
            response.put("success", false);
            response.put("error", error);
            response.put("message", "카카오 로그인에 실패했습니다.");
            return ResponseEntity.badRequest().body(ApiResponse.onFailure("AUTH_ERROR", "카카오 로그인 실패", response));
        }
        
        if (code != null) {
            response.put("success", true);
            response.put("code", code);
            response.put("message", "카카오 로그인이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        }
        
        response.put("success", false);
        response.put("message", "인증 코드가 없습니다.");
        return ResponseEntity.badRequest().body(ApiResponse.onFailure("AUTH_ERROR", "인증 코드 없음", response));
    }

    /**
     * 현재 인증 상태를 확인하는 엔드포인트
     */
    @GetMapping("/status")
    @Operation(summary = "인증 상태 확인", description = "현재 사용자의 인증 상태를 확인합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStatus() {
        log.info("인증 상태 확인 요청 받음");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> status = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            status.put("authenticated", true);
            status.put("username", authentication.getName());
            
            if (authentication.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                status.put("memberId", oAuth2User.getMemberId());
                status.put("nickname", oAuth2User.getNickname());
                status.put("profileImageUrl", oAuth2User.getProfileImageUrl());
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
        log.info("인증 테스트 요청 받음");
        return ResponseEntity.ok(ApiResponse.onSuccess("Auth test endpoint is working!"));
    }
} 