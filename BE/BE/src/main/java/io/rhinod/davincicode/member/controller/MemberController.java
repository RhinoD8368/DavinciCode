package io.rhinod.davincicode.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.member.dto.UserDTO;
import io.rhinod.davincicode.member.service.MemberService;
import io.rhinod.davincicode.member.vo.LoginRequestVO;
import io.rhinod.davincicode.util.ApiResponse;
import io.rhinod.davincicode.util.JwtTokenProvider;
import io.rhinod.davincicode.util.Role;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestVO loginRequest) {
		
		UserDTO user = memberService.findByUserId(loginRequest.getUserId());
		
		System.out.println("############# ??" + user);
		
		
		if (user == null) {
            return ApiResponse.fail("일치하는 사용자가 없습니다. 회원가입을 진행해주세요");
        }
		
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ApiResponse.fail("비밀번호가 일치하지 않습니다.");
        }
		
		String token = jwtTokenProvider.createToken(user.getUserId(), user.getUserRole());
		Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("userId", user.getUserId());
        response.put("role", user.getUserRole().name());

        return ApiResponse.success("로그인 성공", response);
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
