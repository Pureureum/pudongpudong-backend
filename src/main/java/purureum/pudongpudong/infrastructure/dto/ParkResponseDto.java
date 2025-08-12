package purureum.pudongpudong.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import purureum.pudongpudong.domain.model.Park;
import purureum.pudongpudong.domain.model.enums.ParkDifficulty;
import purureum.pudongpudong.infrastructure.dto.api.KakaoNaviApiResponseDto;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParkResponseDto {
	String id;
	String name;
	String description;
	ParkDifficulty difficulty;
	Boolean isVisited;
	Double longitude;
	Double latitude;
	Integer distance;
	Integer time;
	
	public static ParkResponseDto toDto(Park park, KakaoNaviApiResponseDto.Route response) {
		return ParkResponseDto.builder()
				.id(park.getId())
				.name(park.getPlaceName())
				.description(park.getDescription())
				.difficulty(park.getDifficulty())
				.isVisited(false)
				.longitude(park.getLongitude())
				.latitude(park.getLatitude())
				.distance(response.getSummary().getDistance())
				.time(response.getSummary().getDuration()/60)
				.build();
	}
}
