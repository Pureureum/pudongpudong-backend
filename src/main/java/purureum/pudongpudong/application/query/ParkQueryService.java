package purureum.pudongpudong.application.query;

import purureum.pudongpudong.infrastructure.dto.ParkDetailResponseDto;
import purureum.pudongpudong.infrastructure.dto.ParkResponseDto;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ParkQueryService {
	Flux<ParkResponseDto> getAllParksWithMyLocation(Double longitude, Double latitude);
	List<ParkDetailResponseDto> searchParksByKeyword(String keyword);
}
