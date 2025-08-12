package purureum.pudongpudong.application.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import purureum.pudongpudong.domain.model.Park;
import purureum.pudongpudong.domain.model.Review;
import purureum.pudongpudong.domain.repository.ParkRepository;
import purureum.pudongpudong.infrastructure.adapter.api.KakaoNaviApiService;
import purureum.pudongpudong.infrastructure.dto.FairyDto;
import purureum.pudongpudong.infrastructure.dto.ParkDetailResponseDto;
import purureum.pudongpudong.infrastructure.dto.ParkResponseDto;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiRequestDto;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkQueryServiceImpl implements ParkQueryService {
	
	private final ParkRepository parkRepository;
	private final KakaoNaviApiService kakaoNaviApiService;
	
	@Override
	public Flux<ParkResponseDto> getAllParksWithMyLocation(Double longitude, Double latitude) {
		// TODO: 유저 정보 추가하여 반환
		return Mono.fromCallable(parkRepository::findAll)
				.subscribeOn(Schedulers.boundedElastic())
				.flatMapMany(allParks -> {
					List<List<Park>> parks = IntStream.range(0, allParks.size())
							.boxed()
							.collect(Collectors.groupingBy(i -> i/15))
							.values()
							.stream()
							.map(list -> list.stream().map(allParks::get).collect(Collectors.toList()))
							.toList();
					
					return Flux.fromIterable(parks)
							.flatMap(parkList -> {
								List<KakaoNaviApiRequestDto.Destination> destinations = parkList.stream()
										.map(park -> new KakaoNaviApiRequestDto.Destination(
												park.getId(), String.valueOf(park.getLongitude()), String.valueOf(park.getLatitude())
										)).toList();
								
								Mono<KakaoNaviApiResponseDto> responses = kakaoNaviApiService.getWalkingRoutes(destinations, longitude, latitude);
								return Mono.zip(Mono.just(parkList), responses)
										.flatMapMany(t -> {
											
											List<Park> t1 = t.getT1();
											List<KakaoNaviApiResponseDto.Route> t2 = t.getT2().getRoutes();
											
											return Flux.fromIterable(t1)
													.zipWith(Flux.fromIterable(t2))
													.map(tuple -> ParkResponseDto.toDto(tuple.getT1(), tuple.getT2()));
										});
							});
				});
	}
	
	@Override
	public List<ParkDetailResponseDto> searchParksByKeyword(String keyword) {
		
		List<Park> parks = parkRepository.findParksByPlaceNameContaining(keyword);
		
		return parks.stream()
				.map(park -> {
					List<String> tags = park.getParkTags().stream()
							.map(parkTag -> parkTag.getTag().getName())
							.toList();
					
					double averageRating = park.getReviews().stream()
							.mapToDouble(Review::getRating)
							.average()
							.orElse(0.0);
					
					int reviewCount = park.getReviews().size();
					
					return new ParkDetailResponseDto(
							park.getPlaceName(),
							park.getDescription(),
							(park.getFairy() != null ? FairyDto.toDto(park.getFairy()) : null),
							tags,
							park.getDifficulty(),
							averageRating,
							reviewCount
					);
				}).collect(Collectors.toList());
	}
}
