package com.passtival.backend.domain.matching.controller.test;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.entity.MatchingApplicant;
import com.passtival.backend.domain.matching.service.MatchingApplicantService;
import com.passtival.backend.global.auth.model.CustomMemberDetails;
import com.passtival.backend.global.auth.util.JwtUtil;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 인증/인가 테스트용 컨트롤러
 * 개발 및 테스트 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test-API", description = "테스트용 API")
public class AuthTestController {
	private final JwtUtil jwtUtil;
	private final MatchingApplicantService matchingApplicantService;

	/**
	 * JWT 토큰 인증 테스트
	 * @param memberDetails 인증된 사용자 정보
	 * @return 사용자 인증 정보
	 */
	@Operation(
		summary = "JWT 인증 테스트",
		description = "JWT 토큰이 올바르게 인증되는지 테스트합니다. Bearer 토큰이 필요합니다.",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/auth")
	public BaseResponse<AuthTestResponse> testAuth(
		@AuthenticationPrincipal CustomMemberDetails memberDetails) {

		AuthTestResponse response = AuthTestResponse.builder()
			.memberId(memberDetails.getMemberId())
			.username(memberDetails.getUsername())
			.authorities(memberDetails.getAuthorities().toString())
			.message("JWT 토큰 인증 성공")
			.build();

		return BaseResponse.success(response);
	}

	/**
	 * 현재 사용자 정보 조회 테스트
	 * @param memberDetails 인증된 사용자 정보
	 * @return 사용자 상세 정보
	 */
	@Operation(
		summary = "현재 사용자 정보 조회",
		description = "현재 JWT 토큰의 사용자 정보를 조회합니다.",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/me")
	public BaseResponse<UserInfoResponse> getCurrentUser(
		@AuthenticationPrincipal CustomMemberDetails memberDetails) {

		UserInfoResponse response = UserInfoResponse.builder()
			.memberId(memberDetails.getMemberId())
			.username(memberDetails.getUsername())
			.role(memberDetails.getAuthorities().iterator().next().getAuthority())
			.authenticated(true)
			.build();

		return BaseResponse.success(response);
	}

	/**
	 * 토큰 없이 접근 가능한 공개 엔드포인트
	 * @return 공개 접근 테스트 결과
	 */
	@Operation(
		summary = "공개 접근 테스트",
		description = "인증 없이 접근 가능한 엔드포인트"
	)
	@GetMapping("/public")
	public BaseResponse<String> testPublicAccess() {
		return BaseResponse.success("공개 접근 테스트 성공 - 인증 불필요");
	}

	// 응답 DTO 클래스들
	@lombok.Builder
	@lombok.Getter
	public static class AuthTestResponse {
		private Long memberId;
		private String username;
		private String authorities;
		private String message;
	}

	@lombok.Builder
	@lombok.Getter
	public static class UserInfoResponse {
		private Long memberId;
		private String username;
		private String role;
		private boolean authenticated;
	}

	/**
	 * memberId로 테스트용 액세스 토큰 발급
	 * @param memberId 토큰을 발급받을 회원 ID
	 * @return JWT 액세스 토큰
	 */
	@Operation(
		summary = "테스트용 토큰 발급",
		description = "memberId를 받아서 해당 회원의 JWT 토큰을 발급합니다. (테스트 전용)"
	)
	@GetMapping("/token/{memberId}")
	public BaseResponse<String> issueTestToken(@PathVariable Long memberId) {

		// 회원 존재 여부 확인
		MatchingApplicant matchingApplicant = matchingApplicantService.getMemberById(memberId);

		// JWT 토큰 생성
		String accessToken = jwtUtil.createAccessToken(memberId, matchingApplicant.getRole().getAuthority());

		return BaseResponse.success(accessToken);

	}

}
