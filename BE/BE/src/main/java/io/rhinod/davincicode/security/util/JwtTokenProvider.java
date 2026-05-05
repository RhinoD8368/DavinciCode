package io.rhinod.davincicode.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.rhinod.davincicode.member.mapper.MemberMapper;
import io.rhinod.davincicode.security.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";
    
    private final MemberMapper memberMapper;
    
    private Key signKey;
    
    /**<pre>
     * 빈 생성 및 DI 완료 후 실행되는 초기화 메서드이다. 
     * @PostConstruct 어노테이션 역할이다.
     * </pre>
     * */
    @PostConstruct
    protected void init() {
    	byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }
    
    /** <pre>
     * Access Token을 헤더에서 가져온다.
     * </pre>
     * */
    public String getAccessToken(HttpServletRequest request) {
    	String bearerToken = request.getHeader("Authorization");
    	if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
    		return bearerToken.substring(7);
    	}
    	
    	return null;
    }
    
    /** <pre>
     * Refresh Token을 쿠키에서 가져온다.
     * </pre>
     * */
    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    /** Token 생성 */
    private Map<String, Object> buildJwtToken(UserDTO userDTO, long expirationTime, String tokenType) {
    	
    	Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);
        
        /* [ 리프레시토큰을 엑세스토큰으로 사용할 경우 방지 ]
         * 리프레시 토큰을 탈취해서 엑세스토큰으로 사용하면 상대적으로 만료기간이 긴 리프레시토큰을 엑세스 토큰으로 사용할 수 있기 때문에
         * 클레임에 타입 정보를 넣어준다. 만약 클레임을 수정하더라도 시크릿 키가 없으므로 서명이 불일치해진다. 
         * */
    	Map<String, Object> extraClaims = new HashMap<String, Object>();
    	extraClaims.put("role", userDTO.getUserRole());
    	extraClaims.put("type", tokenType);
    	
    	String token = Jwts.builder()
			.setClaims(extraClaims) 
			.setSubject(userDTO.getUserId())
	        .setIssuedAt(now)
	        .setExpiration(validity)
	        .signWith(signKey, SignatureAlgorithm.HS256)
	        .compact();
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	resultMap.put("token", token);
    	resultMap.put("expirationTime", validity);
    	
    	return resultMap;
    }
    /** AccessToken 생성 */
    public Map<String, Object> createAccessToken(UserDTO userDTO) {
        return buildJwtToken(userDTO, accessTokenExpirationTime, ACCESS_TOKEN_TYPE);
    }
    
    /** RefreshToken 생성 */
    public Map<String, Object> createRefreshToken(UserDTO userDTO) {
    	return buildJwtToken(userDTO, refreshTokenExpirationTime, REFRESH_TOKEN_TYPE);
    }
    
    
    
    private JwtParser getJwtParser() {
    	return Jwts.parserBuilder().setSigningKey(signKey).build();
    }
    /** <pre>
     * 받은 토큰으로부터 클레임을 반환한다.
     * 내부적으로 parseClaimsJws 메서드가 토큰의 서명, 구조, 만료시간을 확인하고 Exception을 던진다. 
     * </pre>
     * */
    public Claims getClaimsFromToken(String token) {
    	return getJwtParser().parseClaimsJws(token).getBody();
    }
    /** <pre>
     * 공통 토큰 validation
     * parseClaimsJws 메서드가 토큰의 서명, 구조, 만료시간을 확인하고 Exception을 던진다.
     * </pre>
     * */
    private void validateToken(String token) throws Exception {
    	getJwtParser().parseClaimsJws(token);
    }
    /** AccessToken의 토큰의 서명, 구조, 만료시간을 확인하고 Exception을 던진다. */
    public void validateAccessToken(String token) throws Exception {
    	validateToken(token);
    }
    /** RefreshToken의 토큰의 서명, 구조, 만료시간을 확인하고 Exception을 던진다. */
    public void validateRefreshToken(String token) throws Exception{
    	validateToken(token);
    	
    	String userId = this.getClaimsFromToken(token).getSubject();
    	Map<String, Object> userTokenMap = memberMapper.findRefreshTokenByUserId(userId);
    	String refreshToken = (String) userTokenMap.get("TOKEN");
    	String useYn = (String) userTokenMap.get("USE_YN");
    	
    	if( refreshToken == null || !token.equals(refreshToken) || !"Y".equals(useYn) ) {
    		throw new BadCredentialsException("유효하지 않은 리프레시 토큰이거나 사용 불가능한 상태입니다.");
    	}
    	
    }
}
