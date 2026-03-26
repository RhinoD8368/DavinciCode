package io.rhinod.davincicode.member.controller;

import java.util.HashMap;
import java.util.Map;

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
	
    /** 로그인 처리 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestVO loginRequest) {
		
		String userId   = loginRequest.getUserId();
		String password = loginRequest.getPassword();

		// 1. 아이디/비밀번호 인증 토큰 생성 (미인증 상태)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        // 2. 실제 검증 (이때 CustomUserDetailsService의 loadUserByUsername이 실행됨)
        // 비밀번호가 틀리면 여기서 자동으로 401 에러(또는 예외)가 발생합니다.
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        // authentication 에서 사용자정보 가져오기
        System.out.println("### [MemberController] - login :: " + authentication);
        // ApiResponse.fail("일치하는 사용자가 없습니다. 회원가입을 진행해주세요");
        // ApiResponse.fail("비밀번호가 일치하지 않습니다."); passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())

        // 3. 인증 성공 시 Access Token 생성 (JSON으로 보낼 예정)
//        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        
        // 4. 인증 성공 시 Refresh Token 생성 (쿠키로 보낼 예정)
//        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 5. Refresh Token을 담은 HttpOnly 쿠키 생성
//        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)    // JS에서 접근 불가 (보안)
//                .secure(false)     // HTTPS 환경이라면 true로 변경 (로컬 테스트는 false)
//                .path("/")         // 모든 경로에서 쿠키 유효
//                .maxAge(60 * 60 * 24 * 7) // 7일간 유지
//                .sameSite("Lax")   // CSRF 방어
//                .build();

        // 6. 응답: 바디에는 AccessToken, 헤더에는 RefreshToken 쿠키
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(new LoginResponse(accessToken, "로그인에 성공했습니다."));
        
        // 임시 테스트
        return ApiResponse.success("로그인 성공");
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
		
		return ApiResponse.success("회원가입이 완료되었습니다.",insCnt);
	}
}
