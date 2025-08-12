package purureum.pudongpudong.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import purureum.pudongpudong.domain.model.Park;
import purureum.pudongpudong.domain.model.enums.ParkDifficulty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkDto {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("place_name")
	private String placeName;
	
	@JsonProperty("address_name")
	private String addressName;
	
	@JsonProperty("road_address_name")
	private String roadAddressName;
	
	@JsonProperty("x")
	private String longitude;
	
	@JsonProperty("y")
	private String latitude;
	
	@JsonProperty("place_url")
	private String placeUrl;
	
	public Park toEntity() {
		return Park.builder()
				.id(this.getId())
				.placeName(this.getPlaceName())
				.addressName(this.getAddressName())
				.roadAddressName(this.getRoadAddressName())
				.latitude(Double.parseDouble(this.getLatitude()))
				.longitude(Double.parseDouble(this.getLongitude()))
				.placeUrl(this.getPlaceUrl())
				.description("설명 정보가 없습니다.")
				.difficulty(ParkDifficulty.NORMAL)
				.build();
	}
}
