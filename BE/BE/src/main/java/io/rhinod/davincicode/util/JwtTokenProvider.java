package io.rhinod.davincicode.util;

import java.util.Base64;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInMs;
    
    private Key signKey;

    @PostConstruct
    protected void init() {
    	byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 2. 토큰 생성 (로그인 성공 시 호출)
    public String createToken(String userId, Role role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role.name()); // Enum의 이름(USER, ADMIN 등) 저장

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰에서 인증 정보 조회 (필터에서 사용)
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 4. 토큰에서 유저 아이디 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(signKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 5. 토큰 유효성 검사 (만료 여부, 변조 여부 체크)
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // 만료되었거나 변조된 경우
        }
    }
}
