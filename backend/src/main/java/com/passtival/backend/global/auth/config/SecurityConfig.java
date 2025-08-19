package com.passtival.backend.global.auth.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.passtival.backend.global.auth.filter.JwtAuthenticationFilter;
import com.passtival.backend.global.auth.service.CustomOAuth2UserService;
import com.passtival.backend.global.auth.service.OAuth2SuccessHandler;

import jakarta.servlet.http.HttpServletRequest;

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
	//
	// @Bean
	// public BCryptPasswordEncoder bCryptPasswordEncoder() {
	// 	return new BCryptPasswordEncoder();
	// }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors((cors) -> cors
				.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration configuration = new CorsConfiguration();
						configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));

						configuration.setAllowedMethods(Collections.singletonList("*"));
						configuration.setAllowCredentials(true);
						configuration.setAllowedHeaders(Collections.singletonList("*"));
						configuration.setMaxAge(3600L);
						configuration.setExposedHeaders(Collections.singletonList("Authorization"));
						return configuration;
					}
				}));

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

			// 소셜 로그인 관련 경로
			.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

			// 토큰 관련 API
			.requestMatchers("/api/auth/**").permitAll()

			// 회원가입 완료 API (소셜 로그인 후 호출)
			.requestMatchers("/api/me/profile").authenticated()

			// 특정 역할의 사용자만 허용
			.requestMatchers("/api/matches/**").hasRole("USER")

			// 관리자 API (향후 확장용)
			//.requestMatchers("/api/admin/**").hasRole("ADMIN")

			// 추첨 API (공개)
			.requestMatchers("/api/raffle/**").permitAll()

			// 테스트 API - 완전 공개
			.requestMatchers("/api/test/**").permitAll()

			// 인증(JWT 토큰 필요 없음) 절차 없이 모든 접근을 허용
			.anyRequest().permitAll());

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