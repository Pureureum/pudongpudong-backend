package purureum.pudongpudong.domain.auth.oauth2.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Spring Security에서 OAuth2 로그인 사용자를 나타내는 커스텀 클래스.
 * OAuth2User 인터페이스를 구현하며, 우리 애플리케이션에 필요한 사용자 정보를 추가로 포함합니다.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities; // 사용자에게 부여된 권한 목록
    private final Map<String, Object> attributes; // OAuth2 공급자로부터 받은 원본 사용자 정보 (JSON 형태)
    private final String nameAttributeKey; // 사용자 이름(고유 식별자)을 나타내는 속성 키

    // 우리 애플리케이션의 Member 엔티티와 매핑되는 정보
    private final Long memberId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    /**
     * CustomOAuth2User 생성자.
     *
     * @param authorities      사용자에게 부여된 권한 목록
     * @param attributes       OAuth2 공급자로부터 받은 원본 사용자 정보
     * @param nameAttributeKey 사용자 이름(고유 식별자)을 나타내는 속성 키 (예: "id" for Kakao)
     * @param memberId         우리 애플리케이션의 사용자 고유 ID
     * @param email            사용자 이메일
     * @param nickname         사용자 닉네임
     * @param profileImageUrl  사용자 프로필 이미지 URL
     */
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Long memberId,
                            String email,
                            String nickname,
                            String profileImageUrl) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * OAuth2User 인터페이스의 getName() 메소드 구현.
     * 이 메소드는 nameAttributeKey에 해당하는 값을 반환합니다.
     * 예를 들어, 카카오의 경우 카카오 사용자 고유 ID가 됩니다.
     *
     * @return 사용자 고유 식별자 문자열
     */
    @Override
    public String getName() {
        return attributes.get(nameAttributeKey).toString();
    }
}