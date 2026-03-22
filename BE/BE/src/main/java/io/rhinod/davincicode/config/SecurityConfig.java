package io.rhinod.davincicode.config;

import java.util.Arrays;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.rhinod.davincicode.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@RequiredArgsConstructor // JwtTokenProvider 주입을 위해 추가
public class SecurityConfig {
	
	private final JwtTokenProvider jwtTokenProvider; // 2. 필드 주입
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/join", "/api/auth/login", "/api/auth/signUp").permitAll() // 로그인, 회원가입 관련은 모두 허용
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            
            // JWT 필터를 시큐리티 필터 체인에 등록
            // UsernamePasswordAuthenticationFilter 단계가 오기 전에 JWT 검사부터 하겠다는 뜻입니다.
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), 
                    UsernamePasswordAuthenticationFilter.class);
		
            // 6. OAuth2 로그인 설정 (나중에 구현할 핸들러를 여기에 연결할 예정)
//            .oauth2Login(oauth2 -> oauth2
//                 .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                 .successHandler(oAuth2AuthenticationSuccessHandler)
//            );
		
		return http.build();
	}
	
	// 비밀번호 암호화 빈 등록 (회원가입 시 사용)
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
 // CORS 설정 Bean
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // 리액트 주소
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
