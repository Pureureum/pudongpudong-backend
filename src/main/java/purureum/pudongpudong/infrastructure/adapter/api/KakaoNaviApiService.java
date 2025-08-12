package purureum.pudongpudong.infrastructure.adapter.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiRequestDto;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KakaoNaviApiService {
	
	private final WebClient webClient;
	private final String kakaoApiKey;
	
	public KakaoNaviApiService(WebClient.Builder webClientBuilder,
								@Value("${kakao.navi.url") String kakaoNaviUrl,
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
				.bodyToMono(KakaoNaviApiResponseDto.class);
	}
	
	
}
