package io.rhinod.davincicode.member.service.impl;

import org.springframework.stereotype.Service;

import io.rhinod.davincicode.apiTest.mapper.ApiTestMapper;
import io.rhinod.davincicode.member.dto.SignUpDTO;
import io.rhinod.davincicode.member.dto.UserDTO;
import io.rhinod.davincicode.member.mapper.MemberMapper;
import io.rhinod.davincicode.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
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
}
