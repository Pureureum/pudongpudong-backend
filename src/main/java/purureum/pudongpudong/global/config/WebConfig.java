package purureum.pudongpudong.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 웹 관련 설정을 담당하는 설정 클래스.
 * RestTemplate 등 웹 관련 빈들을 정의합니다.
 */
@Configuration
public class WebConfig {

    /**
     * RestTemplate 빈을 등록합니다.
     * KakaoService 등에서 외부 API 호출 시 사용됩니다.
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 