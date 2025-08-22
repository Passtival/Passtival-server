package com.passtival.backend.domain.authentication.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.authentication.repository.AuthenticationKeyRepository;
import com.passtival.backend.domain.raffle.model.entity.AuthenticationKey;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AuthenticationKeyRepository authenticationKeyRepository;

	/**
	 * 인증키 유효성 검사
	 * @param requestKey 요청에서 받은 인증키
	 * @throws BaseException 인증키가 유효하지 않은 경우
	 */
	public void validateAuthenticationKey(String requestKey) {
		AuthenticationKey authKey = getCurrentAuthenticationKey();

		if (!authKey.getAuthenticationKey().equals(requestKey)) {
			throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
		}
	}

	/**
	 * 현재 인증키 조회
	 * @return 현재 인증키
	 * @throws BaseException 인증키가 없는 경우
	 */
	private AuthenticationKey getCurrentAuthenticationKey() {
		AuthenticationKey authKey = authenticationKeyRepository.findFirstByOrderByIdAsc();

		if (authKey == null) {
			throw new BaseException(BaseResponseStatus.AUTH_KEY_NOT_FOUND);
		}

		return authKey;
	}

	/**
	 * 인증키 갱신 (기존 키 검증 후 새로운 키로 업데이트)
	 * @param newKey 새로운 인증키
	 * @param oldKey 기존인증키 (검증용)
	 * @throws BaseException 인증키 검증 실패, DB 오류 시
	 */
	@Transactional
	public void updateAuthenticationKey(String newKey, String oldKey) {
		// 1. 현재 인증키 조회
		AuthenticationKey currentAuthKey = getCurrentAuthenticationKey();

		// 2. 기존 인증키 검증
		if (!currentAuthKey.getAuthenticationKey().equals(oldKey)) {
			throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
		}

		// 3. 새로운 키와 기존 키가 같은 경우 체크
		if (currentAuthKey.getAuthenticationKey().equals(newKey)) {
			throw new BaseException(BaseResponseStatus.SAME_AUTH_KEY);
		}

		// 4. 기존 키를 새로운 키로 갱신
		currentAuthKey.updateKey(newKey);
		authenticationKeyRepository.save(currentAuthKey);
	}
}
