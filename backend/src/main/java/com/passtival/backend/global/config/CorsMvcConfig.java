package com.passtival.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로에 대해 CORS 설정 적용
			.allowedOrigins(
				"https://passtival.co.kr", // 실제 배포된 클라이언트 도메인
				"https://passtival.cloud:8080",
				"http://passtival.cloud:8080"
			)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
			.allowedHeaders("*") // 모든 헤더 허용
			.exposedHeaders("Authorization") // 클라이언트에서 접근할 수 있는 헤더
			.allowCredentials(true) // 쿠키 허용
			.maxAge(3600); // 1시간

	}
}
