package io.rhinod.davincicode.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import io.rhinod.davincicode.security.service.CustomUserDetailsService;
import io.rhinod.davincicode.security.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	
	/**
	 * Jwt방식에서 UsernamepasswordAuthenticationFilter 앞에 호출되어 인증요청을 하는 필터이다
	 * */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 들어온 요청의 헤더에서 jwt토큰 추출
		String token = resolveToken(request);
		System.out.println("### [JwtAuthenticationFilter] request :: " + request);
		System.out.println("### [JwtAuthenticationFilter] token 추출 :: " + token);
		System.out.println("### [JwtAuthenticationFilter] token 검사 :: " + jwtTokenProvider.validateToken(token));
		
		// 토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtTokenProvider.getUserId(token));
			
            // 토큰이 유효하면 인증 객체(Authentication)를 가져와서 시큐리티 컨텍스트에 저장
            Authentication auth = jwtTokenProvider.getAuthentication(token, userDetails);
            System.out.println("### [JwtAuthenticationFilter] - jwtTokenProvider.getAuthentication :: " + auth);
            
            // 
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("### [JwtAuthenticationFilter] - SecurityContextHolder :: " + SecurityContextHolder.getContext());
        }
		
		// 다음 필터로 진행
		filterChain.doFilter(request, response);
	}
	
	private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
