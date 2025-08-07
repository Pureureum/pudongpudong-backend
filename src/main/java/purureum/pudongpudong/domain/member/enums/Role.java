package purureum.pudongpudong.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할을 정의하는 Enum.
 * Spring Security에서 권한으로 사용될 수 있습니다.
 */
@Getter
@RequiredArgsConstructor // final 필드를 포함하는 생성자를 자동으로 생성
public enum Role {

    // 각 역할은 Spring Security에서 "ROLE_" 접두사가 붙은 형태로 사용됩니다.
    // 예: "ROLE_USER", "ROLE_ADMIN"
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key; // Spring Security에서 사용할 권한 키
    private final String title; // 역할에 대한 설명 (한글명)
}
