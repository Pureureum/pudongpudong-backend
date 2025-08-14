package purureum.pudongpudong.domain.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import purureum.pudongpudong.domain.weather.dto.WeatherRequestDTO;
import purureum.pudongpudong.domain.weather.dto.WeatherResponseDTO;
import purureum.pudongpudong.domain.weather.service.WeatherService;
import purureum.pudongpudong.global.apiPayload.ApiResponse;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 기상청 API를 호출하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "기상청 API 관련 엔드포인트")
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/current")
    @Operation(summary = "동대문구 현재 날씨 조회", description = "동대문구의 현재 날씨를 조회합니다.")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getCurrentWeather() {
        
        log.info("동대문구 현재 날씨 조회 요청 받음");

        WeatherRequestDTO request = new WeatherRequestDTO(); // 기본값으로 동대문구 설정
        WeatherResponseDTO response = weatherService.getCurrentWeather(request);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WEATHER_ERROR", "동대문구 현재 날씨 조회에 실패했습니다.", null));
        }
    }

    @PostMapping("/current/region")
    @Operation(summary = "지역별 현재 날씨 조회", description = "지정한 지역의 현재 날씨를 조회합니다.")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getCurrentWeatherByRegion(
            @Valid @RequestBody WeatherRequestDTO request) {
        
        log.info("지역별 현재 날씨 조회 요청 받음 - 지역코드: {}", request.getRegionCode());

        WeatherResponseDTO response = weatherService.getCurrentWeather(request);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WEATHER_ERROR", "지역별 현재 날씨 조회에 실패했습니다.", null));
        }
    }

    @GetMapping("/current/region/{regionCode}")
    @Operation(summary = "지역별 현재 날씨 조회 (GET)", description = "지역코드로 현재 날씨를 조회합니다.")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getCurrentWeatherByRegionGet(
            @PathVariable String regionCode) {
        
        log.info("지역별 현재 날씨 조회 요청 받음 (GET) - 지역코드: {}", regionCode);

        WeatherRequestDTO request = new WeatherRequestDTO();
        request.setRegionCode(regionCode);

        WeatherResponseDTO response = weatherService.getCurrentWeather(request);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WEATHER_ERROR", "지역별 현재 날씨 조회에 실패했습니다.", null));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "기상청 API 상태 확인", description = "기상청 API 연결 상태를 확인합니다.")
    public ResponseEntity<ApiResponse<String>> getWeatherApiStatus() {
        log.info("기상청 API 상태 확인 요청 받음");

        WeatherRequestDTO testRequest = new WeatherRequestDTO();
        testRequest.setRegionCode("11000"); // 서울
        testRequest.setDate(java.time.LocalDate.now().toString());

        WeatherResponseDTO response = weatherService.getCurrentWeather(testRequest);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess("기상청 API 연결 정상"));
        } else {
            return ResponseEntity.ok(ApiResponse.onSuccess("기상청 API 연결 확인 필요"));
        }
    }

    @GetMapping("/data-time-info")
    @Operation(summary = "데이터 기준 시간 정보", description = "현재 날씨 데이터의 기준 시간과 다음 업데이트 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getDataTimeInfo() {
        log.info("데이터 기준 시간 정보 조회 요청 받음");
        
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        int dataHour = currentMinute < 50 ? currentHour - 1 : currentHour;
        if (dataHour < 0) dataHour = 23;
        
        // 다음 업데이트 시간
        int nextHour = (dataHour + 1) % 24;
        
        // 데이터 경과 시간
        long minutesDiff = currentMinute < 50 ? 
            (60 - currentMinute) : currentMinute - 50;
        
        // 다음 업데이트까지 남은 시간
        long nextUpdateMinutes = currentMinute < 50 ? 
            (50 - currentMinute) : (60 - currentMinute + 50);
        
        var timeInfo = Map.of(
            "현재시각", now.format(DateTimeFormatter.ofPattern("HH:mm")),
            "데이터기준시간", String.format("%02d:00", dataHour),
            "데이터경과시간", minutesDiff + "분 전",
            "다음업데이트시간", String.format("%02d:00", nextHour),
            "다음업데이트까지", "약 " + nextUpdateMinutes + "분 후",
            "참고사항", "기상청 데이터는 매시각 45분 생성, 50분부터 API 제공"
        );
        
        return ResponseEntity.ok(ApiResponse.onSuccess(timeInfo));
    }
}
