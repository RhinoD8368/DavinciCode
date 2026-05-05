package io.rhinod.davincicode.member.service;

import java.util.Map;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.security.dto.UserDTO;

public interface MemberService {

	UserDTO findByUserId(String userId);

	int findByEmail(String email);

	int signUpUser(SignUpDTO signUpDTO);

	Map<String, Object> processLogin(Map<String, Object> paramMap);

	void deleteRefreshToken(String refreshToken); 
}
