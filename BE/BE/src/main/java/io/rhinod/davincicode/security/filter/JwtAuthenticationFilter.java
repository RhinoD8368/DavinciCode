package io.rhinod.davincicode.security.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.rhinod.davincicode.security.util.JwtTokenProvider;
import io.rhinod.davincicode.util.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtTokenProvider jwtTokenProvider;
//	private final CustomUserDetailsService customUserDetailsService;
	
	/**
	 * Jwt방식에서 UsernamepasswordAuthenticationFilter 앞에 호출되어 인증요청을 하는 필터이다
	 * */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		/* ======================================================== */
		String accessToken = jwtTokenProvider.getAccessToken(request);
		
		// 1. Access Token 검사
		try {
			if( accessToken != null ) {
				
				jwtTokenProvider.validateAccessToken(accessToken);
				
				Claims claims = jwtTokenProvider.getClaimsFromToken(accessToken);
				String userId = claims.getSubject();
				String role = claims.get("role", String.class);
				String tokenType = claims.get("type", String.class);
				
				if(!"ACCESS".equals(tokenType)) {
					sendErrorResponse(response, HttpStatus.BAD_REQUEST, "잘못된 토큰 타입입니다.");
					return;
				}
				
				// 매 요청마다 DB 조회를 하지 않고 validateAccessToken이 검증 끝냈으니까 새 시큐리티 기본 User객체를 생성해서 SecurityContextHolder에 넣어준다.
				// UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
				// Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
				
				List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
				UserDetails userDetails = new User(userId, "", authorities);
				Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
//			else {
//				String refreshToken = jwtTokenProvider.getRefreshToken(request);
//				
//				if (refreshToken != null) {
//					
//					jwtTokenProvider.validateRefreshToken(refreshToken);
//					
//	                // 여기서 바로 재발급을 해줄 수도 있고, 
//	                // 혹은 프론트엔드에서 /reissue를 호출하도록 401 에러만 던질 수도 있습니다.
//	                // 보통은 보안상 프론트에서 /reissue를 호출하게 유도하는 것이 정석입니다.
//	            	System.out.println("AccessToken 만료, RefreshToken 존재함. 재발급 필요 상태.");
//	            }
//			}
		}
		catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
            return;
        } catch (BadCredentialsException | JwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
            return;
        }
		
		// 다음 필터로 진행
		filterChain.doFilter(request, response);
	}
	
	/** [ 핵심 ] ApiResponse 규격으로 JSON 응답을 직접 내려주는 메서드 */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

    	String jsonResponse = ApiResponse.convertToJson(false, message);
        
        response.getWriter().write(jsonResponse);
    }
}
