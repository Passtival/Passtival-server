package com.passtival.backend.domain.lostfound.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.lostfound.model.entity.Admin;
import com.passtival.backend.domain.lostfound.model.request.AdminLoginRequest;
import com.passtival.backend.domain.lostfound.repository.AdminRepository;
import com.passtival.backend.global.auth.jwt.JwtUtil;
import com.passtival.backend.global.auth.model.token.TokenResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public TokenResponse login(AdminLoginRequest requestDto) {
		// 관리자 조회
		Admin admin = adminRepository.findByLoginId(requestDto.getLoginId())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.ADMIN_LOGIN_FAILED));

		// 인증키 검증
		if (!passwordEncoder.matches(requestDto.getAuthKey(), admin.getAuthKey())) {
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
