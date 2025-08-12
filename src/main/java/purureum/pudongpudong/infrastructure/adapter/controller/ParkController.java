package purureum.pudongpudong.infrastructure.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import purureum.pudongpudong.application.command.ParkCommandService;
import purureum.pudongpudong.application.query.ParkQueryService;
import purureum.pudongpudong.global.apiPayload.ApiResponse;
import purureum.pudongpudong.infrastructure.dto.ParkResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
@Tag(name = "Park API", description = "공원 페이지와 관련된 API입니다.")
public class ParkController {
	
	private final ParkQueryService parkQueryService;
	private final ParkCommandService parkCommandService;
	
	@Operation(
			summary = "공원 지도 API",
			description = "공원들의 좌표를 조회합니다.")
	@PostMapping
	public Mono<ApiResponse<List<ParkResponseDto>>> getParkMap(@RequestParam Double x,
															   @RequestParam Double y) {
		return parkQueryService.getAllParksWithMyLocation(x, y).collectList().map(ApiResponse::onSuccess);
	}
	
}
