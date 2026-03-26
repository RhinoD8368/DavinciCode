package io.rhinod.davincicode.member.service;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.security.dto.UserDTO;

public interface MemberService {

	UserDTO findByUserId(String userId);

	int findByEmail(String email);

	int signUpUser(SignUpDTO signUpDTO);
 

}
