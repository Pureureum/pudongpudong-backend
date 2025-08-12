package purureum.pudongpudong.infrastructure.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KakaoNaviApiResponseDto {
	
	@JsonProperty("routes")
	private List<Route> routes;
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Route {
		@JsonProperty("result_code")
		private int code;
		
		@JsonProperty("result_msg")
		private int message;
		
		@JsonProperty("summary")
		private Summary summary;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Summary {
		@JsonProperty("distance")
		private int distance;
		
		@JsonProperty("duration")
		private int duration;
	}
	
}
