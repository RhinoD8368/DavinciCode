package io.rhinod.davincicode.member.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.rhinod.davincicode.member.dto.UserDTO;
import io.rhinod.davincicode.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		UserDTO user = memberMapper.findByUserId(userId);
		
		if( user == null ) {
			throw new UsernameNotFoundException("아이디를 찾을 수 없습니다. :: " + userId);
		}
		
		return User.builder()
	            .username(user.getUserId()) // 여기서 이메일 대신 아이디를 넣어줌
	            .password(user.getPassword())
	            .roles(user.getUserRole().name())
	            .build();
	}

}
