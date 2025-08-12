package purureum.pudongpudong.infrastructure.adapter.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import purureum.pudongpudong.global.apiPayload.code.status.ErrorStatus;
import purureum.pudongpudong.global.apiPayload.exception.handler.ApiHandler;
import purureum.pudongpudong.infrastructure.dto.api.KakaoMapApiResponseDto;
import purureum.pudongpudong.infrastructure.dto.ParkDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class KakaoMapApiService {

	private final WebClient webClient;
	private final String kakaoApiKey;
	
	public KakaoMapApiService(WebClient.Builder webClientBuilder,
                              @Value("${kakao.map.url}") String mapApiUrl,
							  @Value("${kakao.api.key}") String kakaoApiKey) {
		this.webClient = webClientBuilder.baseUrl(mapApiUrl).build();
		this.kakaoApiKey = kakaoApiKey;
	}
	
	public Flux<ParkDto> getAllParks() {
		AtomicInteger page = new AtomicInteger(1);
		
		return getParksByPage(page.get())
				.expand(response -> {
					if (response.getMeta().isEnd()) {
						return Mono.empty();
					}
					return getParksByPage(page.getAndIncrement());
				})
				.flatMap(response -> Flux.fromIterable(response.getParks()));
	}
	
	private Mono<KakaoMapApiResponseDto> getParksByPage(int page) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/v2/local/search/keyword.json")
						.queryParam("query", "동대문구 공원")
						.queryParam("rect", "127.009,37.550,127.098,37.600")
						.queryParam("page", page)
						.build())
				.header("Authorization", "KakaoAK " + kakaoApiKey)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response ->
						response.bodyToMono(String.class)
								.map(body -> {
									log.error("카카오 멥 API 호출에 실패 || 4xx 코드 : {}, response body: {}", response.statusCode(), body);
									return new ApiHandler(ErrorStatus.KAKAO_MAP_API_BAD_REQUEST);
								})
								.flatMap(Mono::error)
				)
				.onStatus(HttpStatusCode::is5xxServerError, response ->
						response.bodyToMono(String.class)
								.map(body -> {
									log.error("카카오 맵 API 호출에 실패 || 5xx 코드 : {}, response body: {}", response.statusCode(), body);
									return new ApiHandler(ErrorStatus.KAKAO_MAP_API_SERVER_ERROR);
								})
								.flatMap(Mono::error)
				)
				.bodyToMono(KakaoMapApiResponseDto.class);
	}
}
