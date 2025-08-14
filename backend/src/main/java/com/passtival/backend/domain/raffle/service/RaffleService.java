package com.passtival.backend.domain.raffle.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.raffle.model.entity.Applicant;
import com.passtival.backend.domain.raffle.model.entity.AuthenticationKey;
import com.passtival.backend.domain.raffle.model.request.ApplicantRegistrationRequest;
import com.passtival.backend.domain.raffle.repository.ApplicantRepository;
import com.passtival.backend.domain.raffle.repository.AuthenticationKeyRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaffleService {

	/**
	 * 의존성 주입
	 */
	private final ApplicantRepository applicantRepository;
	private final AuthenticationKeyRepository authenticationKeyRepository;

	/**
	 * 신청자 등록
	 * @param request 신청자 등록 요청 정보
	 */
	public void registerApplicant(ApplicantRegistrationRequest request) throws BaseException {

		// 신청자 등록 로직
		// =========================================================
		// 1. request 값중 key 값을 이용하여 인증키가 유효한지 검사
		// 2. request 값을 이용하여 DB에 존재하는 회원인지 검사
		// 3. 회원이 존재하면 이미 신청한 것으로 간주하고 에러 메시지 반환
		// 4. 회원이 존재하지 않으면 신청자 정보를 DB에 저장
		// =========================================================

		try {
			// 1. 인증키 유효성 검사
			validateAuthenticationKey(request.getKey());

			// 2. 학번 중복 확인
			if (applicantRepository.existsByStudentId(request.getStudentId())) {
				throw new BaseException(BaseResponseStatus.DUPLICATE_APPLICANT);
			}

			// 3. 신청자 등록
			Applicant newApplicant = Applicant.builder()
				.applicantName(request.getApplicantName())
				.studentId(request.getStudentId())
				.build();

			applicantRepository.save(newApplicant);
		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * 인증키 유효성 검사
	 * @param requestKey 요청에서 받은 인증키
	 * @throws BaseException 인증키가 유효하지 않은 경우
	 */
	private void validateAuthenticationKey(String requestKey) throws BaseException {
		try {
			AuthenticationKey authKey = getCurrentAuthenticationKey();

			if (!authKey.getKey().equals(requestKey)) {
				throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
			}

		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 현재 인증키 조회
	 * @return 현재 인증키
	 * @throws BaseException 인증키가 없는 경우
	 */
	private AuthenticationKey getCurrentAuthenticationKey() throws BaseException {
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
	public void updateAuthenticationKey(String newKey, String oldKey) throws BaseException {
		try {
			// 1. 현재 인증키 조회
			AuthenticationKey currentAuthKey = getCurrentAuthenticationKey();

			// 2. 기존 인증키 검증
			if (!currentAuthKey.getKey().equals(oldKey)) {
				throw new BaseException(BaseResponseStatus.INVALID_AUTH_KEY);
			}

			// 3. 새로운 키와 기존 키가 같은 경우 체크
			if (currentAuthKey.getKey().equals(newKey)) {
				throw new BaseException(BaseResponseStatus.SAME_AUTH_KEY);
			}

			// 4. 기존 키를 새로운 키로 갱신
			currentAuthKey.updateKey(newKey);
			authenticationKeyRepository.save(currentAuthKey);

		} catch (BaseException e) {
			throw e; // 비즈니스 로직 예외는 그대로 전파
		}
		catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
