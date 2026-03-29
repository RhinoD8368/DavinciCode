package io.rhinod.davincicode.member.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.member.mapper.MemberMapper;
import io.rhinod.davincicode.member.service.MemberService;
import io.rhinod.davincicode.security.dto.CustomUserDetails;
import io.rhinod.davincicode.security.dto.UserDTO;
import io.rhinod.davincicode.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final AuthenticationManager authenticationManager; // 인증 담당자 역할
	private final JwtTokenProvider jwtTokenProvider; // jwt 토큰관련 유틸
//	private final PasswordEncoder passwordEncoder; // 비밀번호 인코딩용
	private final MemberMapper memberMapper;
	
	@Override
	public UserDTO findByUserId(String userId){
		return memberMapper.findByUserId(userId);
	}

	@Override
	public int findByEmail(String email) {
		// TODO Auto-generated method stub
		return memberMapper.findByEmail(email);
	}

	@Override
	public int signUpUser(SignUpDTO signUpDTO) {
		return memberMapper.signUpUser(signUpDTO);
	}
	
	/**<pre>
	 * 로그인 프로세스
	 * 1. UsernamePasswordAuthenticationToken 생성 (AuthenticationManager의 인증에 필요)
	 * 2. 사용자 인증
	 * 3. Access Token 생성
	 * 4. Refresh Token 생성
	 * 5. Refresh Token 저장
	 * </pre>
	 * */
	@Override
	public Map<String, Object> processLogin(Map<String, Object> paramMap) {
		
		// 1. AuthenticationManager가 인증할 때 필요한 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(paramMap);

        // 2. 사용자 인증
        UserDTO userDTO = authenticateUser(authenticationToken);
        
        // 3. 인증 성공 시 Access Token 생성 (JSON으로 보냄)
        Map<String, Object> accessTokenMap = jwtTokenProvider.createAccessToken(userDTO);
        
        // 4. 인증 성공 시 Refresh Token 생성 (쿠키로 보냄)
        Map<String, Object> refreshTokenMap = jwtTokenProvider.createRefreshToken(userDTO);
        
        userDTO.setPassword(null);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("userDTO", userDTO);
        resultMap.put("accessToken", accessTokenMap.get("token"));
        resultMap.put("refreshToken", refreshTokenMap.get("token"));
        resultMap.put("refreshExpirationTime", refreshTokenMap.get("expirationTime"));
        
        // 5. Refresh Token 저장
        memberMapper.saveRefreshToken(resultMap);
		
        return resultMap;
	}
	
	/**<pre>
	 * UsernamePasswordAuthenticationToken 생성
	 * 검증의 주체인 authenticationManager가 인증을 요청할 때 필요한 토큰생성
	 * </pre>
	 * @return UsernamePasswordAuthenticationToken
	 * */
	private UsernamePasswordAuthenticationToken createAuthenticationToken(Map<String, Object> paramMap) {
		String userId   = (String) paramMap.get("userId");
		String password = (String) paramMap.get("password");
		
		return new UsernamePasswordAuthenticationToken(userId, password);
	}
	
	/**<pre>
	 * authenticationManager를 통해 사용자 인증 후 사용자정보를 반환
	 * 사용자가 입력한 평문 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교
     * 내부적으로 BCryptPasswordEncoder.matches()로 비교
     * 비밀번호가 틀리면 여기서 자동으로 401 에러(또는 예외)가 발생합니다.
	 * </pre>
	 * @return UserDTO
	 * */
	private UserDTO authenticateUser(UsernamePasswordAuthenticationToken token) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticationManager.authenticate(token).getPrincipal();
		return customUserDetails.getUserDTO();
	}
}
