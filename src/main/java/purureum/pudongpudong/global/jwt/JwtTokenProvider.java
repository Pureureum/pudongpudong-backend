package purureum.pudongpudong.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰을 생성, 검증 및 파싱하는 유틸리티 클래스.
 */
@Slf4j // 로깅을 위한 Lombok 어노테이션
@Component // Spring 빈으로 등록
public class JwtTokenProvider {

    private final Key key; // JWT 서명에 사용할 키
    private final long accessTokenExpirationMillis; // Access Token 만료 시간 (밀리초)
    private final long refreshTokenExpirationMillis; // Refresh Token 만료 시간 (밀리초)

    // application.yml (또는 application-secret.yml)에서 JWT 시크릿 키와 만료 시간을 주입받습니다.
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis,
                            @Value("${jwt.refresh-token-expiration-millis}") long refreshTokenExpirationMillis) {
        // Base64 인코딩된 시크릿 키를 사용하여 Key 객체 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    /**
     * Access Token을 생성합니다.
     *
     * @param memberId 사용자 고유 ID
     * @param email    사용자 이메일
     * @return 생성된 Access Token
     */
    public String createAccessToken(Long memberId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) // 토큰의 주체 (여기서는 memberId)
                .claim("email", email) // 클레임 추가 (이메일)
                .claim("type", "access") // 토큰 타입
                .setIssuedAt(now) // 발행 시간
                .setExpiration(validity) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘과 키
                .compact();
    }

    /**
     * Refresh Token을 생성합니다.
     *
     * @param memberId 사용자 고유 ID
     * @param email    사용자 이메일
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(Long memberId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("email", email)
                .claim("type", "refresh") // 토큰 타입
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보를 가져옵니다.
     *
     * @param token JWT 토큰 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // TODO: 실제 애플리케이션에서는 사용자 역할(Role) 정보를 클레임에 추가하고 여기서 파싱하여 권한을 부여합니다.
        // 현재는 기본 권한만 부여하거나, CustomOAuth2User에서 가져온 권한을 사용하도록 합니다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(",")) // 예시: "ROLE_USER" 권한 부여
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체 생성 (Spring Security의 User 객체 사용)
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    /**
     * JWT 토큰에서 클레임(Claims)을 파싱합니다.
     *
     * @param accessToken JWT 토큰 문자열
     * @return 파싱된 Claims 객체
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 클레임은 가져올 수 있도록 예외 처리
            return e.getClaims();
        }
    }
}