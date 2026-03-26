package io.rhinod.davincicode.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.rhinod.davincicode.security.service.CustomUserDetailsService;
import io.rhinod.davincicode.util.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	
	private final CustomUserDetailsService customUserDetailsService;
	
	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;
    
    private Key signKey;
    
    @PostConstruct
    protected void init() {
    	byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // AccessToken 생성 (로그인 성공 시 호출)
    public String createAccessToken(String userId, Role role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role.name()); // Enum의 이름(USER, ADMIN 등) 저장

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // RefreshToken 생성
    public String createRefreshToken(String userId) {
        Date now = new Date();
        // AccessToken보다 훨씬 길게 설정 14일
        Date validity = new Date(now.getTime() + (expirationTime * 24 * 14)); 

        return Jwts.builder()
                .setSubject(userId) // 리프레시 토큰은 권한 정보 없이 ID만 담아도 충분
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 인증 정보 조회 (필터에서 사용)
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 유저 아이디 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(signKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 유효성 검사 (만료 여부, 변조 여부 체크)
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // 만료되었거나 변조된 경우
        }
    }

	
    
}
