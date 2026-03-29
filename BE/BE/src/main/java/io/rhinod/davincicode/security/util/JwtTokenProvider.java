package io.rhinod.davincicode.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.rhinod.davincicode.security.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	
	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.access.expiration-time}")
    private long accessTokenExpirationTime;
    
    @Value("${jwt.token.refresh.expiration-time}")
    private long refreshTokenExpirationTime;
    
    private Key signKey;
    
    @PostConstruct
    protected void init() {
    	byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** AccessToken */
    public Map<String, Object> createAccessToken(UserDTO userDTO) {
        return buildJwtToken(userDTO, accessTokenExpirationTime);
    }
    
    /** RefreshToken */
    public Map<String, Object> createRefreshToken(UserDTO userDTO) {
    	return buildJwtToken(userDTO, refreshTokenExpirationTime);
    }
    
    /** Token 생성 */
    private Map<String, Object> buildJwtToken(UserDTO userDTO, long expirationTime) {
    	
    	Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);
        
    	Map<String, Object> extraClaims = new HashMap<String, Object>();
    	extraClaims.put("role", userDTO.getUserRole());
    	
    	String token = Jwts.builder()
			.setSubject(userDTO.getUserId())
	        .setClaims(extraClaims) 
	        .setIssuedAt(now)
	        .setExpiration(validity)
	        .signWith(signKey, SignatureAlgorithm.HS256)
	        .compact();
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	resultMap.put("token", token);
    	resultMap.put("expirationTime", validity);
    	
    	return resultMap;
    }

    /** <pre>
     * 토큰에서 인증 정보 조회 (필터에서 사용)
     * customUserDetailsService.loadUserByUsername 에서 DB조회로 사용자 객체를 가져온다.
     * </pre>
     * */
    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 유저 아이디 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(signKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /** <pre>
     * 토큰 유효성 검사
     * 1. 서버의 secretKey로 서명된 진짜 토큰인지 검증
     * 2. 토큰 만료기간 검증
     * </pre>
     */
    public boolean validateToken(String token) {
        try {
        	// 1. 서버의 secretKey로 서명된 진짜 토큰인지 검증
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
            
            // 2. 토큰 만료기간 검증
            return !claims.getBody().getExpiration().before(new Date());
        } 
        
        // 만료되었거나 변조된 경우
        catch (Exception e) {
            return false; 
        }
    }
}
