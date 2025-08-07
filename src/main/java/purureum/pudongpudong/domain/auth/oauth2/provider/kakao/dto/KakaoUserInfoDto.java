package purureum.pudongpudong.domain.auth.oauth2.provider.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 카카오 사용자 정보 API 응답을 매핑하는 DTO.
 * Lombok 어노테이션을 사용하여 Getter, Setter, ToString을 자동으로 생성합니다.
 * JsonIgnoreProperties를 통해 JSON에 존재하지만 DTO에 정의되지 않은 필드는 무시합니다.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // DTO에 정의되지 않은 필드는 무시
public class KakaoUserInfoDto {

    private Long id; // 카카오 사용자 고유 ID

    @JsonProperty("kakao_account") // JSON 필드명과 DTO 필드명이 다를 경우 사용
    private KakaoAccount kakaoAccount; // 카카오 계정 정보

    /**
     * 카카오 계정 정보를 담는 내부 클래스.
     */
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;

        @JsonProperty("profile")
        private Profile profile; // 프로필 정보

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;

        private String email; // 이메일 주소

        // 추가적으로 필요한 정보가 있다면 여기에 필드를 추가할 수 있습니다.
        // 예: phone_number, age_range, birthday 등 (동의 항목 설정 필요)
    }

    /**
     * 프로필 정보를 담는 내부 클래스.
     */
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname; // 닉네임

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl; // 썸네일 이미지 URL

        @JsonProperty("profile_image_url")
        private String profileImageUrl; // 프로필 이미지 URL

        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
    }
}
