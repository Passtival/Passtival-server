package com.passtival.backend.domain.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.passtival.backend.domain.admin.model.request.AuthenticationLevelRequest;
import com.passtival.backend.domain.admin.model.response.AuthenticationKeyResponse;
import com.passtival.backend.domain.admin.model.response.WinnerResponse;
import com.passtival.backend.domain.admin.service.AdminAuthenticationService;
import com.passtival.backend.domain.admin.service.AdminRaffleService;
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

	private final AdminAuthenticationService adminAuthenticationService;
	private final AdminRaffleService adminRaffleService;

	// 인증키 조회
	@GetMapping("authentication-key")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(
		summary = "관리자 인증키 조회",
		description = "관리자(학생회)가 인증키 레벨 설정 후 학생에게 인증키를 보여줄때 사용하는 api"
	)
	public ResponseEntity<BaseResponse<AuthenticationKeyResponse>> getAuthenticationKey() {
		return ResponseEntity.ok(BaseResponse.success(adminAuthenticationService.getAuthenticationKey()));
	}

	// 인증키 레벨 설정
	@PatchMapping("authentication-key")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(
		summary = "관리자 인증키 레벨 설정",
		description = "관리자(학생회)가 인증키 레벨 설정 - 학생이 인증키 입력 후 레벨 비교를 위해 필요"
	)
	public ResponseEntity<BaseResponse<Void>> setAuthenticationKeyLevel(@RequestBody AuthenticationLevelRequest request) {
		adminAuthenticationService.setAuthenticationKeyLevel(request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@PostMapping("/{day}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "관리자 일차별 추첨 실행",
		description = "관리자(학생회)가 일별 추첨을 진행하는 api | day은 1~3",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public ResponseEntity<BaseResponse<Void>> executeRaffleByDay(@PathVariable int day) {
		adminRaffleService.executeRaffleByDay(day);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	// 일차별 당점차 조회
	@GetMapping("/{day}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(
		summary = "관리자 일차별 당첨자 조회",
		description = "관리자(학생회)가 일별 추첨 후에 당첨자를 조회하는 api | day은 1~3",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public ResponseEntity<BaseResponse<WinnerResponse>> getRaffleWinnersByDay(@PathVariable int day) {
		WinnerResponse response = adminRaffleService.getRaffleWinnersByDay(day);
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@PostMapping("/premium")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "관리자 프리미엄 추첨 실행",
		description = "관리자(학생회)가 프리미엄 추첨을 진행하는 api",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public ResponseEntity<BaseResponse<Void>> executeRaffleOfPremium() {
		adminRaffleService.executeRaffleOfPremium();
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@GetMapping("/premium")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "관리자 프리미엄 당첨자 조회",
		description = "관리자(학생회)가 프리미엄 추첨 후에 당첨자를 조회하는 api",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public ResponseEntity<BaseResponse<WinnerResponse>> getRaffleWinnerOfPremium() {
		WinnerResponse response = adminRaffleService.getRaffleWinnerOfPremium();
		return ResponseEntity.ok(BaseResponse.success(response));
	}

}
