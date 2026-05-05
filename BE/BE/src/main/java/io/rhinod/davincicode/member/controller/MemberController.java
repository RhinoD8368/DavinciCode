package io.rhinod.davincicode.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.member.service.MemberService;
import io.rhinod.davincicode.member.vo.LoginRequestVO;
import io.rhinod.davincicode.util.ApiResponse;
import io.rhinod.davincicode.util.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {
	
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    
    @Value("${jwt.token.refresh.expiration-time}")
    private long refreshTokenExpirationTime;
	
    /** <pre>
     * 로그인 처리 
     * 1. processLogin 호출
     * 2. HttpOnly 쿠키 생성
     * 3. Header 생성
     * 4. body 데이터
     * </pre>
     * */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestVO loginRequest) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", loginRequest.getUserId());
		paramMap.put("password", loginRequest.getPassword());
		
		// 1. processLogin 호출
		Map<String, Object> resultMap = memberService.processLogin(paramMap);
         
        // 2. RefreshToken을 담은 HttpOnly 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", (String) resultMap.get("refreshToken"))
                .httpOnly(true)    // JS에서 접근 불가 (보안)
                .secure(false)     // HTTPS 환경이라면 true 로 변경 (로컬 테스트는 false)
                .path("/")         // 모든 경로에서 쿠키 유효
                .maxAge(refreshTokenExpirationTime / 1000) // 7일간 유지
                .sameSite("Lax")   // CSRF 방어
                .build();
        
        // 3. Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // refresh Token
        
        // 4. body 데이터
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("accessToken", resultMap.get("accessToken"));
        dataMap.put("userDTO", resultMap.get("userDTO"));
        
        System.out.println("### [MemberController] - headers :: " + headers);
        System.out.println("### [MemberController] - dataMap :: " + dataMap);

        // 응답 :: 바디에는 AccessToken, 헤더에는 RefreshToken 쿠키
        return ApiResponse.success(headers, dataMap, "로그인에 성공했습니다.");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		
		System.out.println("### [logout] - request :: " + request);
		
		// 1. 쿠키에서 리프레시 토큰 추출
		String refreshToken = getTokenFromRequest(request, "refreshToken");
		
		System.out.println("ref token :: " + refreshToken);
		
		if (refreshToken != null) {
	        // 2. DB에서 토큰 무효화 (삭제 또는 USE_YN = 'N')
			memberService.deleteRefreshToken(refreshToken);
	    }

	    // 3. 브라우저의 쿠키 강제 삭제 (만료 시간을 0으로 설정)
	    Cookie cookie = new Cookie("refreshToken", null);
	    cookie.setMaxAge(0);
	    cookie.setPath("/");
	    cookie.setHttpOnly(true);
	    response.addCookie(cookie);
		
		return ApiResponse.success(null, "로그아웃이 완료되었습니다.");
	}
	
	@PostMapping("/signUp")
	public ResponseEntity<?> signUp( @RequestBody SignUpDTO signUpDTO ) {
		
		// 한 사람을 구분하는 최소단위는 이메일이다.
		
		// 이메일 중복확인
		int emailCount = memberService.findByEmail(signUpDTO.getEmail());
		if(emailCount > 0) {
			return ApiResponse.fail("중복된 이메일이 존재합니다.");
		}
		
		// 회원가입
		String encodedPassword = passwordEncoder.encode(signUpDTO.getPassword());
		signUpDTO.setPassword(encodedPassword);
		signUpDTO.setUserRole(Role.USER);
		
		System.out.println("### [/api/auth/signUp] - signUpDTO :: " + signUpDTO);
		
		int insCnt = memberService.signUpUser(signUpDTO);
		
		return ApiResponse.success(insCnt, "회원가입이 완료되었습니다.");
	}
	
	private String getTokenFromRequest(HttpServletRequest request, String tokenName) {
		
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (tokenName.equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}
}
