package purureum.pudongpudong.domain.member.repository;

import purureum.pudongpudong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Member 엔티티에 대한 데이터베이스 접근을 위한 Spring Data JPA 레포지토리.
 * JpaRepository를 상속받아 기본적인 CRUD 메소드를 자동으로 제공받습니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 주어진 provider와 providerId로 Member를 조회합니다.
     * CustomOAuth2UserService에서 소셜 로그인 사용자를 식별하는 데 사용됩니다.
     * @param provider OAuth2 제공자 (예: "kakao")
     * @param providerId OAuth2 제공자에서 발급한 사용자 고유 ID
     * @return 조회된 Member 객체 (Optional)
     */
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}
