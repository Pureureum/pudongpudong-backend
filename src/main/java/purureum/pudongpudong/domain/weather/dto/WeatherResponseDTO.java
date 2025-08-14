package purureum.pudongpudong.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 기상청 API 응답을 매핑하는 DTO
 */
@Data
public class WeatherResponseDTO {

    @JsonProperty("response")
    private Response response;
    
    // 데이터 기준 시간 정보 (사용자에게 표시용)
    private String dataBaseDate;        // 데이터 기준 날짜 (예: 2025-01-27)
    private String dataBaseTime;        // 데이터 기준 시간 (예: 14:00)
    private String dataAge;             // 데이터 경과 시간 (예: 30분 전)
    private String nextUpdateTime;      // 다음 업데이트 시간 (예: 15:00)
    private String nextUpdateIn;        // 다음 업데이트까지 남은 시간 (예: 약 20분 후)

    @Data
    public static class Response {
        @JsonProperty("header")
        private Header header;
        
        @JsonProperty("body")
        private Body body;
    }

    @Data
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;
        
        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        @JsonProperty("items")
        private Items items;
        
        @JsonProperty("numOfRows")
        private Integer numOfRows;
        
        @JsonProperty("pageNo")
        private Integer pageNo;
        
        @JsonProperty("totalCount")
        private Integer totalCount;
    }

    @Data
    public static class Items {
        @JsonProperty("item")
        private List<WeatherItem> item;
    }

    @Data
    public static class WeatherItem {
        @JsonProperty("baseDate")
        private String baseDate;
        
        @JsonProperty("baseTime")
        private String baseTime;
        
        @JsonProperty("category")
        private String category; // 기상 요소 (예: TMP, REH, WSD)
        
        @JsonProperty("fcstDate")
        private String fcstDate; // 예보 날짜
        
        @JsonProperty("fcstTime")
        private String fcstTime; // 예보 시간
        
        @JsonProperty("fcstValue")
        private String fcstValue; // 예보 값
        
        @JsonProperty("nx")
        private String nx; // X 좌표
        
        @JsonProperty("ny")
        private String ny; // Y 좌표
    }
}
