package purureum.pudongpudong.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*; // JPA 관련 어노테이션 임포트
import purureum.pudongpudong.domain.member.enums.Role;

/**
 * 사용자 정보를 나타내는 JPA 엔티티.
 * 데이터베이스의 'member' 테이블과 매핑됩니다.
 */
@Getter
@NoArgsConstructor // 기본 생성자 자동 생성 (JPA 엔티티에 필수)
@Entity // JPA 엔티티임을 선언
@Table(name = "member") // 매핑될 테이블 이름 지정
public class Member {

    @Id // 기본 키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증분 전략 (MySQL의 AUTO_INCREMENT와 유사)
    private Long id; // 사용자 고유 ID (우리 애플리케이션 내부 ID)

    @Column(nullable = false) // NULL을 허용하지 않음
    private String provider; // OAuth2 제공자 (예: "kakao", "naver", "google")

    @Column(nullable = false)
    private String providerId; // OAuth2 제공자에서 발급한 사용자 고유 ID (예: 카카오 ID)

    @Column(nullable = false, unique = true) // NULL을 허용하지 않고, 유니크 제약 조건 추가
    private String email; // 사용자 이메일

    @Column(nullable = false)
    private String nickname; // 사용자 닉네임

    @Column
    private String profileImageUrl; // 사용자 프로필 이미지 URL (NULL 허용)

    @Enumerated(EnumType.STRING) // Enum을 DB에 문자열 형태로 저장
    @Column(nullable = false)
    private Role role; // 사용자 역할 (USER, ADMIN 등)

    @Builder // 빌더 패턴을 사용하여 객체 생성 가능
    public Member(String provider, String providerId, String email, String nickname, String profileImageUrl, Role role) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    /**
     * 사용자 정보(닉네임, 프로필 이미지)를 업데이트하는 메소드.
     * CustomOAuth2UserService에서 기존 사용자 정보 업데이트 시 사용됩니다.
     * @param nickname 새로운 닉네임
     * @param profileImageUrl 새로운 프로필 이미지 URL
     */
    public void update(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
