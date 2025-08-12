package purureum.pudongpudong.infrastructure.dto.api;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoNaviApiRequestDto {
	
	private Origin origin;
	private List<Destination> destinations;
	private int radius;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Origin {
		private String x;
		private String y;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Destination {
		private String key;
		private String x;
		private String y;
	}
}
