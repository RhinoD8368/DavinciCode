package io.rhinod.davincicode.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.rhinod.davincicode.member.mapper.MemberMapper;
import io.rhinod.davincicode.security.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		System.out.println("### [CustomUserDetailsService] - loadUserByUsername :: " + userId);

		UserDTO user = memberMapper.findByUserId(userId);
		System.out.println("### [CustomUserDetailsService] - UserDTO :: " + user);
		
		if( user == null ) {
			throw new UsernameNotFoundException("### [CustomUserDetailsService] - UsernameNotFoundException :: " + userId);
		}
		
		// 꽉 채우자
		UserDetails userDetails = User.builder()
	            .username(user.getUserId())
	            .password(user.getPassword())
	            .roles(user.getUserRole().name())
	            .build();
		System.out.println("### [CustomUserDetailsService] - 리턴하는 UserDetails :: " + userDetails);
		
		return userDetails;
	}

}
