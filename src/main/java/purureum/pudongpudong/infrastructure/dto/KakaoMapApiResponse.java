package purureum.pudongpudong.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KakaoMapApiResponse {
	@JsonProperty("documents")
	private List<ParkDto> parks;
	
	@JsonProperty("meta")
	private MetaDto meta;
}
