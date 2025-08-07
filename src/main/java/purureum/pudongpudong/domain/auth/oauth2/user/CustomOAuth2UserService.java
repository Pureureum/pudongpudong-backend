package purureum.pudongpudong.domain.auth.oauth2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import purureum.pudongpudong.domain.auth.oauth2.provider.kakao.KakaoService;
import purureum.pudongpudong.domain.auth.oauth2.provider.kakao.dto.KakaoUserInfoDto;
import purureum.pudongpudong.domain.member.entity.Member;
import purureum.pudongpudong.domain.member.enums.Role;
import purureum.pudongpudong.domain.member.repository.MemberRepository;

import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 로그인 시 사용자 정보를 처리하는 서비스.
 * 소셜 로그인 후 사용자 정보를 DB에 저장하거나 업데이트합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 시도: {}", userRequest.getClientRegistration().getRegistrationId());

        // OAuth2 제공자 정보 가져오기
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // 제공자별 사용자 정보 처리
        OAuth2User oAuth2User;
        switch (provider.toLowerCase()) {
            case "kakao":
                oAuth2User = processKakaoUser(accessToken);
                break;
            default:
                throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }

        return oAuth2User;
    }

    /**
     * 카카오 사용자 정보를 처리합니다.
     */
    private OAuth2User processKakaoUser(String accessToken) {
        // 카카오 API로부터 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getKakaoUserInfo(accessToken);
        
        // 카카오 사용자 정보에서 필요한 데이터 추출
        String providerId = String.valueOf(kakaoUserInfo.getId());
        String email = kakaoUserInfo.getKakaoAccount().getEmail();
        String nickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();
        String profileImageUrl = kakaoUserInfo.getKakaoAccount().getProfile().getProfileImageUrl();

        // DB에서 기존 사용자 조회
        Member member = memberRepository.findByProviderAndProviderId("kakao", providerId)
                .orElseGet(() -> {
                    // 새 사용자 생성
                    Member newMember = Member.builder()
                            .provider("kakao")
                            .providerId(providerId)
                            .email(email)
                            .nickname(nickname)
                            .profileImageUrl(profileImageUrl)
                            .role(Role.USER)
                            .build();
                    return memberRepository.save(newMember);
                });

        // 기존 사용자인 경우 정보 업데이트
        if (member.getId() != null) {
            member.update(nickname, profileImageUrl);
            memberRepository.save(member);
        }

        // CustomOAuth2User 객체 생성 및 반환
        return new CustomOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey())),
                Map.of("id", providerId, "email", email, "nickname", nickname),
                "id",
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
} 