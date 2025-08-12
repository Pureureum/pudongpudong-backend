package purureum.pudongpudong.infrastructure.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import purureum.pudongpudong.infrastructure.dto.ParkDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KakaoMapApiResponseDto {
	@JsonProperty("documents")
	private List<ParkDto> parks;
	
	@JsonProperty("meta")
	private MetaDto meta;
}
