package com.passtival.backend.domain.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.admin.model.request.AuthenticationLevelRequest;
import com.passtival.backend.domain.admin.model.response.AuthenticationKeyResponse;
import com.passtival.backend.domain.admin.service.AdminAuthenticationService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/raffle")
@Tag(name = "관리자 추첨 API")
@SecurityRequirement(name = "jwtAuth")
public class AdminRaffleController {

	// 1. 인증키 조회
	// 2. 추첨
	// 3. 응모 결과 조회

	private final AdminAuthenticationService adminAuthenticationService;

	// 인증키 조회
	@GetMapping("authentication-key")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "관리자 인증키 조회")
	public BaseResponse<AuthenticationKeyResponse> getAuthenticationKey() {
		return BaseResponse.success(adminAuthenticationService.getAuthenticationKey());
	}

	// 인증키 레벨 설정
	@PatchMapping("authentication-key")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "관리자 인증키 레벨 설정")
	public BaseResponse<Void> setAuthenticationKeyLevel(@RequestBody AuthenticationLevelRequest request) {
		adminAuthenticationService.setAuthenticationKeyLevel(request);
		return BaseResponse.success(null);
	}

}
