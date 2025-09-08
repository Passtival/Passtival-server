package com.passtival.backend.domain.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.admin.model.request.AdminLoginRequest;
import com.passtival.backend.domain.admin.service.AdminAuthService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.security.model.token.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 API")
public class AdminAuthController {
	private final AdminAuthService adminAuthService;

	@Operation(
		summary = "관리자 로그인",
		description = "분실물 관리자 계정으로 로그인하여 JWT 토큰을 발급받습니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "관리자 로그인 요청 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = AdminLoginRequest.class),
				examples = @ExampleObject(
					name = "관리자 로그인 요청 예시",
					value = """
						{
						    "adminId": "admin",
						    "authKey": "String"
						}
						"""
				)
			)
		)
	)
	@PostMapping("/login")
	public BaseResponse<TokenResponse> login(
		@Valid @RequestBody AdminLoginRequest requestDto) {

		TokenResponse tokenResponse = adminAuthService.login(requestDto);
		return BaseResponse.success(tokenResponse);
	}

	@Operation(
		summary = "관리자 인증 테스트",
		description = "관리자 엑세스 토큰이 올바르게 생성되었는지 테스트를 위한 api",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/test")
	public BaseResponse<Map<String, Object>> test(Authentication authentication) {
		Map<String, Object> result = new HashMap<>();
		result.put("authenticated", authentication != null);

		if (authentication != null) {
			result.put("principal", authentication.getPrincipal().getClass().getSimpleName());
			result.put("authorities", authentication.getAuthorities());
			result.put("name", authentication.getName());
		}

		return BaseResponse.success(result);
	}
}
