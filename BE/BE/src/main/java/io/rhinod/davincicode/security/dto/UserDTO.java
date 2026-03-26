package io.rhinod.davincicode.security.dto;

import java.time.LocalDateTime;

import io.rhinod.davincicode.util.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Long userNo;          
	private String userId;      
	private String password;     
	private String nickname;
    private String email;   
    private Role userRole;  
    private String provider;     
    private String providerId; 
    private String useYn;
    private String createdBy; 
    private LocalDateTime createdAt;  
    private String updatedBy; 
    private LocalDateTime updatedAt; 
    
}
