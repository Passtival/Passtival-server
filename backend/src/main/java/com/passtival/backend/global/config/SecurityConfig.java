package com.passtival.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.passtival.backend.global.auth.filter.JwtAuthenticationFilter;
import com.passtival.backend.global.auth.handler.OAuth2SuccessHandler;
import com.passtival.backend.global.auth.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// private final AuthenticationConfiguration authenticationConfiguration;
	// private final JwtUtil jwtUtil;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
		CustomOAuth2UserService customOAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) {
		//this.authenticationConfiguration = authenticationConfiguration;
		//this.jwtUtil = jwtUtil;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customOAuth2UserService = customOAuth2UserService;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
	}

	//커스텀 로그인 미 구현(확장성 고려 주석처리)
	// @Bean
	// public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
	// 	return configuration.getAuthenticationManager();
	// }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults()); // CORS 설정: CorsMvcConfig의 설정
		http.csrf((auth) -> auth.disable());

		http.formLogin((auth) -> auth.disable());
		http.httpBasic((auth) -> auth.disable());

		//oauth2
		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2SuccessHandler));

		http.authorizeHttpRequests((auth) -> auth

			// 소셜 로그인 관련 경로 (공개)
			.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

			// 토큰 관련 API (공개)
			.requestMatchers("/api/auth/**").permitAll()

			// Swagger UI 및 OpenAPI 문서 (공개)
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

			// 회원가입 완료 API (공개) (소셜 로그인 후 호출)
			.requestMatchers("/api/me/profile").authenticated()

			// 매칭 관련 로직 로그인한 사용자만 (유저)
			.requestMatchers("/api/matching/**").hasRole("USER")

			// 관리자 API -> /api/admin/seed(공연, 메뉴, 부스 다)도 admin이라서 로그인 해야함
			.requestMatchers("/api/admin/login").permitAll()
			// (관리자)
			.requestMatchers("/api/admin/**").hasRole("ADMIN")

			//분실물 로직 (공개) -> 준선님이 수정 예정
			.requestMatchers("/api/found-items/**").permitAll()

			//인증키 변경 로직 (관리자)
			.requestMatchers("/api/authentication/**").hasRole("ADMIN")

			//축제 정보 관련 로직 (공개)
			.requestMatchers("/api/festival**").permitAll()

			// 추첨 API (공개)
			.requestMatchers("/api/raffle/**").permitAll()

			// 테스트 API - (공개)
			.requestMatchers("/api/test/**").permitAll()

			// 모든 요청 로그인 후로 변경 잘못된 요청 전부 방어
			.anyRequest().authenticated());

		//커스텀 로그인 미 구현(확장성 고려 주석처리)
		// LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
		// loginFilter.setFilterProcessesUrl("/api/auth/login");
		// http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http.sessionManagement((session) -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}