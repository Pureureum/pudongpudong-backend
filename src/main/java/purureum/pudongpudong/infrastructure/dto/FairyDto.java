package purureum.pudongpudong.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import purureum.pudongpudong.domain.model.Fairy;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FairyDto {
	String name;
	String description;
	
	public static FairyDto toDto(Fairy fairy) {
		return FairyDto.builder()
				.name(fairy.getName())
				.description(fairy.getDescription())
				.build();
	}
}
