package purureum.pudongpudong.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import purureum.pudongpudong.domain.model.enums.ParkDifficulty;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkDetailResponseDto {
	String name;
	String description;
	FairyDto fairy;
	List<String> tags;
	ParkDifficulty difficulty;
	double averageRating;
	int reviewCount;
	// TODO: Boolean isVisited; 추가
	// TODO: distance, time 필요 시 추가
}
