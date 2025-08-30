package com.passtival.backend.domain.admin.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.admin.model.entity.Admin;
import com.passtival.backend.domain.admin.model.request.AdminLoginRequest;
import com.passtival.backend.domain.admin.repository.AdminRepository;
import com.passtival.backend.global.auth.model.token.TokenResponse;
import com.passtival.backend.global.auth.util.JwtUtil;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

	private final AdminRepository adminRepository;
	private final JwtUtil jwtUtil;

	public TokenResponse login(AdminLoginRequest requestDto) {
		// 관리자 조회
		Admin admin = adminRepository.findByLoginId(requestDto.getAdminId())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.ADMIN_LOGIN_FAILED));

		// 인증키(다른 로그인 기준 password) 검증
		if (!requestDto.getAuthKey().equals(admin.getAuthKey())) {
			throw new BaseException(BaseResponseStatus.ADMIN_LOGIN_FAILED);
		}

		// JWT 토큰 생성
		String accessToken = jwtUtil.createAccessToken(
			admin.getAdminId(),
			"ROLE_" + admin.getRole().toString()
		);
		String refreshToken = jwtUtil.createRefreshToken(
			admin.getAdminId(),
			"ROLE_" + admin.getRole().toString()
		);

		return TokenResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
