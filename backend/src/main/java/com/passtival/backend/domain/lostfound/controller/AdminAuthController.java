package com.passtival.backend.domain.lostfound.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.lostfound.model.request.AdminLoginRequest;
import com.passtival.backend.domain.lostfound.service.AdminAuthService;
import com.passtival.backend.global.auth.model.token.TokenResponse;
import com.passtival.backend.global.common.BaseResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {
	private final AdminAuthService adminAuthService;

	@PostMapping("/login")
	public BaseResponse<TokenResponse> login(
		@Valid @RequestBody AdminLoginRequest requestDto) {

		TokenResponse tokenResponse = adminAuthService.login(requestDto);
		return BaseResponse.success(tokenResponse);
	}

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
