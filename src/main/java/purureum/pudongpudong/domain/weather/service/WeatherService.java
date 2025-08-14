package purureum.pudongpudong.domain.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import purureum.pudongpudong.domain.weather.dto.WeatherRequestDTO;
import purureum.pudongpudong.domain.weather.dto.WeatherResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 기상청 API와 통신하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.kma.service-key}")
    private String serviceKey;

    @Value("${weather.kma.base-url}")
    private String baseUrl;

    public WeatherResponseDTO getCurrentWeather(WeatherRequestDTO request) {
        try {
            String baseTime = calculateBaseTime();
            String baseDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 시간 계산
            LocalDateTime dataTime = calculateDataTime();
            LocalDateTime nextUpdateTime = calculateNextUpdateTime();

            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/getUltraSrtNcst")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", 1)
                    .queryParam("numOfRows", 1000)
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", getNxFromRegionCode(request.getRegionCode()))
                    .queryParam("ny", getNyFromRegionCode(request.getRegionCode()))
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                WeatherResponseDTO responseBody = parseXmlResponse(response.getBody());
                if (responseBody != null) {
                    setWeatherDataTimeInfo(responseBody, dataTime, nextUpdateTime);
                    return responseBody;
                }
            }
            return null;

        } catch (Exception e) {
            log.error("현재 날씨 조회 중 오류 발생", e);
            return null;
        }
    }

    /**
     * base_time 계산 (기상청 API 규격에 맞춤)
     */
    private String calculateBaseTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 기상청 API는 매시각 45분에 생성되어 발표시각으로부터 10분 후부터 API 제공합니다.
        if (minute < 45) {
            hour = hour - 1;
        }
        
        if (hour < 0) {
            hour = 23;
        }
        return String.format("%02d%02d", hour, 0);
    }

    /**
     * 지역코드로 nx 좌표 return
     */
    private String getNxFromRegionCode(String regionCode) {
        switch (regionCode) {
            case "27596": return "61"; // 동대문구
            case "11000": return "60"; // 서울
            case "26000": return "55"; // 부산
            case "27000": return "89"; // 대구
            case "28000": return "76"; // 인천
            case "29000": return "74"; // 광주
            case "30000": return "55"; // 대전
            case "31000": return "124"; // 울산
            case "41000": return "62"; // 경기
            case "42000": return "89"; // 강원
            case "43000": return "89"; // 충북
            case "44000": return "55"; // 충남
            case "45000": return "89"; // 전북
            case "46000": return "55"; // 전남
            case "47000": return "89"; // 경북
            case "48000": return "55"; // 경남
            case "50000": return "124"; // 제주
            default: return "61"; // 기본값 (동대문구)
        }
    }

    /**
     * 지역코드로 내 좌표 return함
     */
    private String getNyFromRegionCode(String regionCode) {
        switch (regionCode) {
            case "27596": return "128"; // 동대문구
            case "11000": return "127"; // 서울
            case "26000": return "128"; // 부산
            case "27000": return "91"; // 대구
            case "28000": return "124"; // 인천
            case "29000": return "74"; // 광주
            case "30000": return "127"; // 대전
            case "31000": return "102"; // 울산
            case "41000": return "120"; // 경기
            case "42000": return "73"; // 강원
            case "43000": return "119"; // 충북
            case "44000": return "124"; // 충남
            case "45000": return "91"; // 전북
            case "46000": return "127"; // 전남
            case "47000": return "91"; // 경북
            case "48000": return "127"; // 경남
            case "50000": return "33"; // 제주
            default: return "128"; // 기본값 (동대문구)
        }
    }

    /**
     * 데이터 기준 시간 계산 (사용자에게 표시용)
     */
    private LocalDateTime calculateDataTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 기상청 API는 매시각 45분에 생성되어 발표시각으로부터 10분 후부터 API 제공
        if (minute < 50) {
            hour = hour - 1;
        }
        
        if (hour < 0) {
            hour = 23;
        }

        return LocalDateTime.of(now.toLocalDate(), java.time.LocalTime.of(hour, 0));
    }

    /**
     * 다음 업데이트 시간 계산
     */
    private LocalDateTime calculateNextUpdateTime() {
        LocalDateTime now = LocalDateTime.now();
        int nextHour = now.getHour() + 1;
        
        if (nextHour >= 24) {
            nextHour = 0;
        }
        
        return LocalDateTime.of(now.toLocalDate(), java.time.LocalTime.of(nextHour, 0));
    }

    /**
     * XML 응답을 파싱하여 WeatherResponseDTO 생성
     */
    private WeatherResponseDTO parseXmlResponse(String xmlResponse) {
        try {
            // 간단한 XML 파싱 (일단 이정도만.. 향후 수정)
            if (xmlResponse.contains("<resultCode>00</resultCode>")) {
                
                // 성공일떄
                WeatherResponseDTO response = new WeatherResponseDTO();
                
                // 기본 응답 구조 생성
                WeatherResponseDTO.Response responseWrapper = new WeatherResponseDTO.Response();
                WeatherResponseDTO.Header header = new WeatherResponseDTO.Header();
                header.setResultCode("00");
                header.setResultMsg("NORMAL_SERVICE");
                responseWrapper.setHeader(header);
                
                response.setResponse(responseWrapper);
                return response;
            } else if (xmlResponse.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                return createDummyWeatherResponse();
            } else {
                // 기타 에러 응답인 경우
                log.error("기상청 API 에러 응답: {}", xmlResponse);
                return null;
            }
        } catch (Exception e) {
            log.error("XML 응답 파싱 중 오류 발생", e);
            return null;
        }
    }
    
    /**
     * 테스트용 더미 날씨 데이터
     */
    private WeatherResponseDTO createDummyWeatherResponse() {
        WeatherResponseDTO response = new WeatherResponseDTO();
        
        // 기본 응답 구조 생성
        WeatherResponseDTO.Response responseWrapper = new WeatherResponseDTO.Response();
        WeatherResponseDTO.Header header = new WeatherResponseDTO.Header();
        header.setResultCode("00");
        header.setResultMsg("DUMMY_DATA_FOR_TESTING");
        responseWrapper.setHeader(header);
        
        // 더미 날씨 데이터
        WeatherResponseDTO.Body body = new WeatherResponseDTO.Body();
        WeatherResponseDTO.Items items = new WeatherResponseDTO.Items();
        
        // 기온, 습도, 풍속 등 더미 데이터
        WeatherResponseDTO.WeatherItem tempItem = new WeatherResponseDTO.WeatherItem();
        tempItem.setCategory("TMP");
        tempItem.setFcstValue("25");
        tempItem.setBaseDate("20250814");
        tempItem.setBaseTime("1400");
        
        WeatherResponseDTO.WeatherItem humidityItem = new WeatherResponseDTO.WeatherItem();
        humidityItem.setCategory("REH");
        humidityItem.setFcstValue("65");
        humidityItem.setBaseDate("20250814");
        humidityItem.setBaseTime("1400");
        
        items.setItem(java.util.List.of(tempItem, humidityItem));
        body.setItems(items);
        responseWrapper.setBody(body);
        
        response.setResponse(responseWrapper);
        return response;
    }

    /**
     * 날씨 응답에 데이터 기준 시간 정보 설정
     */
    private void setWeatherDataTimeInfo(WeatherResponseDTO response, LocalDateTime dataTime, LocalDateTime nextUpdateTime) {
        LocalDateTime now = LocalDateTime.now();
        
        // 데이터 기준 날짜와 시간
        response.setDataBaseDate(dataTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        response.setDataBaseTime(dataTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        
        // 데이터 경과 시간 계산
        long minutesDiff = java.time.Duration.between(dataTime, now).toMinutes();
        if (minutesDiff < 60) {
            response.setDataAge(minutesDiff + "분 전");
        } else {
            long hoursDiff = minutesDiff / 60;
            response.setDataAge(hoursDiff + "시간 전");
        }
        
        // 다음 업데이트 시간
        response.setNextUpdateTime(nextUpdateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        
        // 다음 업데이트까지 남은 시간
        long nextUpdateMinutes = java.time.Duration.between(now, nextUpdateTime).toMinutes();
        if (nextUpdateMinutes < 0) {
            nextUpdateMinutes += 24 * 60; // 다음날로 넘어가는 경우
        }
        
        if (nextUpdateMinutes < 60) {
            response.setNextUpdateIn("약 " + nextUpdateMinutes + "분 후");
        } else {
            long nextUpdateHours = nextUpdateMinutes / 60;
            response.setNextUpdateIn("약 " + nextUpdateHours + "시간 후");
        }
    }
}
