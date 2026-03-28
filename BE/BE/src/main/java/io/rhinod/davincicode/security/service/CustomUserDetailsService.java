package io.rhinod.davincicode.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.rhinod.davincicode.member.mapper.MemberMapper;
import io.rhinod.davincicode.security.dto.CustomUserDetails;
import io.rhinod.davincicode.security.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		UserDTO user = memberMapper.findByUserId(userId);
		System.out.println("### [CustomUserDetailsService] - loadUserByUsername - UserDTO :: " + user);
		
		if( user == null ) {
			throw new UsernameNotFoundException("### [CustomUserDetailsService] - UsernameNotFoundException :: " + userId);
		}
		return new CustomUserDetails(user);
	}

}
