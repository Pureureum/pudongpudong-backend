package purureum.pudongpudong.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetaDto {

	@JsonProperty("is_end")
	private boolean isEnd;
	
	@JsonProperty("total_count")
	private boolean totalCount;
}