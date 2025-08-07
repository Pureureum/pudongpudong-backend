package purureum.pudongpudong.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import purureum.pudongpudong.domain.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import purureum.pudongpudong.domain.auth.oauth2.handler.OAuth2LoginFailureHandler;
import purureum.pudongpudong.domain.auth.oauth2.user.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final String[] WHITELIST = {
			"/api/auth/**",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/swagger-resources/**",
			"/oauth2/**",
			"/login/oauth2/code/**",
			"/oauth/kakao/callback",
			"/login/**",
			"/" // 루트 경로도 허용
	};

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

	// 생성자를 통해 필요한 서비스와 핸들러를 주입받습니다.
	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
						  OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
						  OAuth2LoginFailureHandler oAuth2LoginFailureHandler) {
		this.customOAuth2UserService = customOAuth2UserService;
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
		this.oAuth2LoginFailureHandler = oAuth2LoginFailureHandler;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(WHITELIST).permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 설정 추가
								.userInfoEndpoint(userInfo -> userInfo
										.userService(customOAuth2UserService) // OAuth2 사용자 정보를 처리할 서비스 지정
								)
								.successHandler(oAuth2LoginSuccessHandler) // OAuth2 로그인 성공 시 처리할 핸들러 지정
								.failureHandler(oAuth2LoginFailureHandler) // OAuth2 로그인 실패 시 처리할 핸들러
				);
		
		return http.build();
	}
}