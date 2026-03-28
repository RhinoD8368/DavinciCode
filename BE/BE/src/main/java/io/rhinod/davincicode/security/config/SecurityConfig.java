package io.rhinod.davincicode.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.rhinod.davincicode.security.filter.JwtAuthenticationFilter;
import io.rhinod.davincicode.security.service.CustomUserDetailsService;
import io.rhinod.davincicode.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Dependency Injection (생성자 주입)
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
	
	/** 비밀번호 암호화 객체 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/** Security 인증 매니저 
	 * @param AuthenticationConfiguration
	 * @return AuthenticationManager
	 * @apiNote
	 * <pre>
	 * AuthenticationConfiguration 객체는 Spring Security에서 Bean으로 등록된 객체
	 * AuthenticationManager 객체는 Bean 등록될 때 Spring Container가 알아서 파라미터 넣어서 생성
	 * 
	 * 나중에 로그인 컨트롤러에서 인증을 하기 위해서 호출되며 동작과정은 다음과 같다.
	 * 1. 인증을 위해 AuthenticationManager 호출하고 내부적으로 적절한 AuthenticationProvider에게 인증 부탁
	 * 2. AuthenticationProvider는 DB에 있는 실제 정보가 필요하니 UserDetailsService를 호출함
	 * 3. 만약 기본적인 formLogin(세션방식)으로 진행하면 인증을하기위해 UsernamePasswordAuthenticationFilter를 부른다.
	 * 4-1. UsernamePasswordAuthenticationFilter는 시큐리티에서 제공하는 UserDetailsService를 사용해서 사용자 정보를 가져온다.
	 * 4-2. 제공되는 UserDetailsService는 우리의 DB와 컬럼 정보를 모르기 때문에 스프링 구동시 발급되는 Using generated security password 값을 입력하라고 뜬다.
	 * 5-1. 우리는 JWT(무상태) 방식을 사용하기 때문에 formLogin을 disable 처리한다.
	 * 5-2. 따라서 세션방식의 기본 필터인 UsernamePasswordAuthenticationFilter 필터가 스킵되기 때문에
	 * 5-3. 새로운 사용자확인 주체(?)인 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 이전에 실행되게 끔 설정했다.
	 * 5-4. JwtAuthenticationFilter는 토큰을 추출하고 유효성 검사 후 다음 필터로 넘어가는 행위를 한다.
	 * 5-5. 최초 로그인 시 요청 헤더에 토큰정보가 없기 때문에 다음 필터로 넘어가서 로그인에 해당하는 컨트롤러의 엔드포인트로 넘어간다.
	 * 5-6. 엔드포인트에서 DB정보와 매치확인하고 accessToken refreshToken을 발급해서 클라이언트에 전달해준다.
	 * 5-7. 이후 요청에서는 요청 헤더에 토큰정보가 있어서 JwtAuthenticationFilter에서 토큰을 추출하고 유효한지 확인 후 인증객체를 SecurityContextHolder에 담는다.
	 * 5-8. 인증객체는 Authentication이고 이 객체는 SecurityContext 객체에 담기고 SecurityContext는 SecurityContextHolder에 담긴다.
	 * 5-9. 이후 해당 요청의 엔드포인트에 가서 비지니스 로직이 수행된다.
	 * 
	 * SecurityContextHolder는 내부적으로 ThreadLocal을 써서 요청 하나마다 고유의 쓰레드 저장소를 가져서 편리하게 꺼내쓸 수 있다.
	 * 1개 요청 = 1개 쓰레드 = 1개 독립된 저장소
	 * </pre>
	 * */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// csrf 설정
		http.csrf(csrf -> csrf.disable());
		
		// cors 설정
		http.cors(cors -> cors.configurationSource(corsConfigurationSource));
		
		// session 설정
		// 스프링시큐리티의 동작방식을 무상태(STATELESS) 방식으로 변경한다.
		// 만약 변경하지 않으면 스프링시큐리티 기본 동작방식인 세션-쿠키 방식으로 동작
		// 따라서 formLogin도 disable처리
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		// formLogin 방식 설정
		http.formLogin(form -> form.disable());
		
		// httpBasic 설정
		http.httpBasic(basic -> basic.disable());
		
		// 요청url 접근(인가)권한 설정
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/", "/login", "/join").permitAll()
			.requestMatchers("/api/auth/login", "/api/auth/signUp").permitAll()
			.anyRequest().authenticated()
		);
		
		// jwt stateless 방식으로 UsernamePasswordAuthenticationFilter 앞에 커스텀 인증필터를 둔다.
		http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);
		
		
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsConfiSource() {
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
