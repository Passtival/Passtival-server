package com.passtival.backend.domain.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.admin.model.response.AuthenticationKeyResponse;
import com.passtival.backend.domain.admin.service.AdminAuthenticationService;
import com.passtival.backend.global.common.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/raffle")
public class AdminRaffleController {

	// 1. 인증키 조회
	// 2. 추첨
	// 3. 응모 결과 조회

	private final AdminAuthenticationService adminAuthenticationService;

	// 인증키 조회
	@GetMapping("authentication-key")
	@PreAuthorize("hasRole('ADMIN')")
	public BaseResponse<AuthenticationKeyResponse> getAuthenticationKey() {
		return BaseResponse.success(adminAuthenticationService.getAuthenticationKey());
	}

}
