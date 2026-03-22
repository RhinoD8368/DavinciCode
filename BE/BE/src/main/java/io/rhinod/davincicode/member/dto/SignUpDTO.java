package io.rhinod.davincicode.member.dto;

import io.rhinod.davincicode.util.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpDTO {
	private String userId;
	private String password;
	private String email;
	private String nickname;
	private Role userRole;
}
