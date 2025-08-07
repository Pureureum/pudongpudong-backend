package purureum.pudongpudong.domain.auth.oauth2.provider.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import purureum.pudongpudong.domain.auth.oauth2.provider.kakao.dto.KakaoUserInfoDto;

/**
 * 카카오 API와 통신하여 사용자 정보를 가져오는 서비스 클래스.
 */
@Service
public class KakaoService {

    // application.yml에 설정된 카카오 사용자 정보 URI를 주입받습니다.
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    private final RestTemplate restTemplate;

    // RestTemplate을 주입받아 사용합니다.
    // (Spring 5부터는 WebClient 사용이 권장되지만, 여기서는 간단한 예시를 위해 RestTemplate 사용)
    public KakaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 카카오 Access Token을 사용하여 사용자 정보를 요청하고 DTO로 매핑합니다.
     *
     * @param accessToken 카카오로부터 발급받은 Access Token
     * @return 카카오 사용자 정보 DTO (KakaoUserInfoDto)
     */
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // Access Token을 Bearer 타입으로 설정
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); // Content-Type 설정
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // JSON 응답을 받도록 Accept 헤더 설정

        // HTTP 엔티티 생성 (헤더만 포함, 바디는 없음)
        HttpEntity<String> kakaoRequest = new HttpEntity<>(headers);

        // RestTemplate을 사용하여 카카오 사용자 정보 API 호출
        // KAKAO_USER_INFO_URI로 GET 요청을 보내고, 응답을 KakaoUserInfoDto.class 타입으로 받습니다.
        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                KAKAO_USER_INFO_URI,
                HttpMethod.GET,
                kakaoRequest,
                KakaoUserInfoDto.class
        );

        // 응답 본문(body)에서 KakaoUserInfoDto 객체를 반환합니다.
        return response.getBody();
    }
}