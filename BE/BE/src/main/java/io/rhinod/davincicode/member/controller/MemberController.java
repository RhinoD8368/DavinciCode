package io.rhinod.davincicode.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.member.service.MemberService;
import io.rhinod.davincicode.member.vo.LoginRequestVO;
import io.rhinod.davincicode.security.dto.CustomUserDetails;
import io.rhinod.davincicode.security.dto.UserDTO;
import io.rhinod.davincicode.security.util.JwtTokenProvider;
import io.rhinod.davincicode.util.ApiResponse;
import io.rhinod.davincicode.util.Role;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {
	
	private final AuthenticationManager authenticationManager;
	private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
	
    /** <pre>
     * 로그인 처리 
     * 1. 아이디/비밀번호 인증 토큰 생성 
     * 2. 아이디/비밀번호 검증
     * 3. (인증성공 시)Access Token 생성
     * 4. (인증성공 시)Refresh Token 생성	
     * 5. 각 토큰을 담을 곳 생성
     * </pre>
     * */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestVO loginRequest) {
		
		String userId   = loginRequest.getUserId();
		String password = loginRequest.getPassword();

		// 1. AuthenticationManager가 인증할 때 필요한 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        /* 2. 실제 검증 (이때 CustomUserDetailsService의 loadUserByUsername이 실행됨)
         * 사용자가 입력한 평문 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교
         * 내부적으로 BCryptPasswordEncoder.matches()로 비교
         * 비밀번호가 틀리면 여기서 자동으로 401 에러(또는 예외)가 발생합니다.
         * */
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDTO userDTO = customUserDetails.getUserDTO();
        
        // 3. 인증 성공 시 Access Token 생성 (JSON으로 보냄)
        String accessToken = jwtTokenProvider.createAccessToken(userDTO);
        
        // 4. 인증 성공 시 Refresh Token 생성 (쿠키로 보냄)
        String refreshToken = jwtTokenProvider.createRefreshToken(userDTO);

        // 5. RefreshToken을 담은 HttpOnly 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)    // JS에서 접근 불가 (보안)
                .secure(false)     // HTTPS 환경이라면 true 로 변경 (로컬 테스트는 false)
                .path("/")         // 모든 경로에서 쿠키 유효
                .maxAge(60 * 60 * 24 * 7) // 7일간 유지
                .sameSite("Lax")   // CSRF 방어
                .build();
        
        // Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // refresh Token
        
        // body 데이터
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("accessToken", accessToken);
        dataMap.put("userDTO", userDTO);
        
        System.out.println("### [MemberController] - headers :: " + headers);
        System.out.println("### [MemberController] - dataMap :: " + dataMap);

        // 응답 :: 바디에는 AccessToken, 헤더에는 RefreshToken 쿠키
        return ApiResponse.success(headers, dataMap, "로그인에 성공했습니다.");
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
}
