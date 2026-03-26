package io.rhinod.davincicode.member.mapper;

import org.apache.ibatis.annotations.Mapper;

import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.security.dto.UserDTO;

@Mapper
public interface MemberMapper {

	UserDTO findByUserId(String userId);

	int findByEmail(String email);

	int signUpUser(SignUpDTO signUpDTO);

}
