package io.rhinod.davincicode.member.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 기본 생성자 추가 (Jackson용)
@AllArgsConstructor // 전체 생성자 추가
public class LoginRequestVO {
	private String userId;
	private String password;
}
