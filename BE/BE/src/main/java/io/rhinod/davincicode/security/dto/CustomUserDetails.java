package io.rhinod.davincicode.security.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
	
	/* 직렬화 안할건데 워닝 뜨는거 싫고
	 * @SuppressWarnings도 달기 싫다
	 */
	private static final long serialVersionUID = 1L;
	
	private final UserDTO userDTO;

    public CustomUserDetails(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    // 컨트롤러에서 유저 정보를 통째로 꺼낼 때 사용
    public UserDTO getUserDTO() {
        return userDTO;
    }

    @Override
    public String getUsername() {
        return userDTO.getUserId(); // DTO의 ID 필드 연결
    }

    @Override
    public String getPassword() {
        return userDTO.getPassword(); // DTO의 Password 필드 연결
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role 정보를 시큐리티 규격에 맞게 변환
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDTO.getUserRole().name()));
    }

    // 계정 활성화 여부를 DTO의 useYn으로 체크
    @Override
    public boolean isEnabled() {
        return "Y".equals(userDTO.getUseYn());
    }

    // 나머지 메서드들 (기본값 true 설정)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
