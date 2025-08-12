package purureum.pudongpudong.infrastructure.adapter.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import purureum.pudongpudong.global.apiPayload.code.status.ErrorStatus;
import purureum.pudongpudong.global.apiPayload.exception.handler.ApiHandler;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiRequestDto;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KakaoNaviApiService {
	
	private final WebClient webClient;
	private final String kakaoApiKey;
	
	public KakaoNaviApiService(WebClient.Builder webClientBuilder,
								@Value("${kakao.navi.url}") String kakaoNaviUrl,
								@Value("${kakao.api.key}") String kakaoApiKey) {
		this.webClient = webClientBuilder.baseUrl(kakaoNaviUrl).build();
		this.kakaoApiKey = kakaoApiKey;
	}
	
	public Mono<KakaoNaviApiResponseDto> getWalkingRoutes(List<KakaoNaviApiRequestDto.Destination> destinations, double longitude, double latitude) {
		if(destinations.size() > 15) {
			return Mono.error(new IllegalArgumentException("목적지는 최대 15개만 지정할 수 있습니다."));
		}
		
		KakaoNaviApiRequestDto requestBody = new KakaoNaviApiRequestDto();
		requestBody.setOrigin(new KakaoNaviApiRequestDto.Origin(String.valueOf(longitude), String.valueOf(latitude)));
		requestBody.setDestinations(destinations.stream()
				.map(d -> new KakaoNaviApiRequestDto.Destination(d.getKey(), d.getX(), d.getY()))
				.collect(Collectors.toList()));
		requestBody.setRadius(5000);
		
		return webClient.post()
				.uri("/v1/destinations/directions")
				.header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response ->
						response.bodyToMono(String.class)
								.map(body -> {
									log.error("카카오 네비 API 호출에 실패 || 4xx 코드 : {}, response body: {}", response.statusCode(), body);
									return new ApiHandler(ErrorStatus.KAKAO_NAVI_API_BAD_REQUEST);
								})
								.flatMap(Mono::error)
				)
				.onStatus(HttpStatusCode::is5xxServerError, response ->
						response.bodyToMono(String.class)
								.map(body -> {
									log.error("카카오 네비 API 호출에 실패 || 5xx 코드 : {}, response body: {}", response.statusCode(), body);
									return new ApiHandler(ErrorStatus.KAKAO_NAVI_API_SERVER_ERROR);
								})
								.flatMap(Mono::error)
				)
				.bodyToMono(KakaoNaviApiResponseDto.class);
	}
	
	
}
