package purureum.pudongpudong.domain.weather.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 기상청 API 요청을 위한 DTO
 */
@Data
public class WeatherRequestDTO {

    // 동대문구 지역코드 (27596)
    private static final String DONGDAEMUN_CODE = "27596";
    
    private String regionCode;
    private String date;
    
    // 기본값으로 동대문구 설정
    public WeatherRequestDTO() {
        this.regionCode = DONGDAEMUN_CODE;
        this.date = java.time.LocalDate.now().toString();
    }

    public String getRegionCode() {
        return regionCode != null ? regionCode : DONGDAEMUN_CODE;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
