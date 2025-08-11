package purureum.pudongpudong.application.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import purureum.pudongpudong.domain.model.Park;
import purureum.pudongpudong.domain.repository.ParkRepository;
import purureum.pudongpudong.infrastructure.adapter.api.KakaoMapApiService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkCommandServiceImpl implements ParkCommandService{
	
	private final ParkRepository parkRepository;
	private final KakaoMapApiService kakaoMapApiService;
	
	@Override
	public Flux<Park> saveAllParks() {
		return kakaoMapApiService.getAllParks()
				.filter(park -> park.getAddressName().startsWith("서울 동대문구"))
				.flatMap(parkDto -> {
					Park park = parkDto.toEntity();
					return Mono.just(parkRepository.save(park));
		});
	}
}
