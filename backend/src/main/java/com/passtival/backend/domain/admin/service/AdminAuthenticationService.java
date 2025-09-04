package com.passtival.backend.domain.admin.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.admin.model.request.AuthenticationLevelRequest;
import com.passtival.backend.domain.admin.model.response.AuthenticationKeyResponse;
import com.passtival.backend.domain.authenticationkey.model.AuthenticationKey;
import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthenticationService {

	private final AuthenticationKeyRepository authenticationKeyRepository;

	/**
	 * 현재 사용 가능한 인증키를 조회합니다.
	 * @return 상위 인증키
	 * @throws BaseException 인증키가 없는 경우
	 */
	public AuthenticationKeyResponse getAuthenticationKey() {
		AuthenticationKey authenticationKey = authenticationKeyRepository.findFirstByOrderByIdAsc();

		if (authenticationKey == null) {
			// 인증키를 모두 사용했을 때
			throw new BaseException(BaseResponseStatus.AUTH_KEY_NOT_FOUND);
		}

		return new AuthenticationKeyResponse(authenticationKey.getAuthenticationKey());
	}

	public void setAuthenticationKeyLevel(AuthenticationLevelRequest request) {

		Integer level = request.getLevel();

		if (level == null || level < 1 || level > 3) {
			throw new BaseException(BaseResponseStatus.BAD_REQUEST);
		}

		AuthenticationKey authenticationKey = authenticationKeyRepository.findFirstByOrderByIdAsc();
		authenticationKey.setLevel(level);
		authenticationKeyRepository.save(authenticationKey);
	}
}
