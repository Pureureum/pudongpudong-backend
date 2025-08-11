package purureum.pudongpudong.infrastructure.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import purureum.pudongpudong.global.apiPayload.ApiResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
@Tag(name = "Park API", description = "공원 페이지와 관련된 API입니다.")
public class ParkController {
	
	@Operation(
			summary = "공원 지도 API",
			description = "공원들의 좌표를 조회합니다.")
	@PostMapping
	public ApiResponse<String> getParkMap(Long memberId,
										  double x, double y) {
		
		return ApiResponse.onSuccess("");
	}
	
}
