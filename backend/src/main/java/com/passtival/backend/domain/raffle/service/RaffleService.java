package com.passtival.backend.domain.raffle.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.raffle.model.entity.Applicant;
import com.passtival.backend.domain.raffle.model.entity.AuthenticationKey;
import com.passtival.backend.domain.raffle.model.request.ApplicantRegistrationRequest;
import com.passtival.backend.domain.raffle.repository.ApplicantRepository;
import com.passtival.backend.domain.raffle.repository.AuthenticationKeyRepository;

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
	public void registerApplicant(ApplicantRegistrationRequest request) {

		// 신청자 등록 로직
		// =========================================================
		// 1. request 값중 key 값을 이용하여 인증키가 유효한지 검사
		// 2. request 값을 이용하여 DB에 존재하는 회원인지 검사
		// 3. 회원이 존재하면 이미 신청한 것으로 간주하고 에러 메시지 반환
		// 4. 회원이 존재하지 않으면 신청자 정보를 DB에 저장
		// =========================================================

		try {
			// 1. 인증키 유효성 검사
			if (!isValidAuthenticationKey(request.getKey())) {
				log.warn("유효하지 않은 인증키입니다 - key: {}", request.getKey());
				throw new RuntimeException("유효하지 않은 인증키입니다.");
			}

			// 2. 학번으로 기존 신청자 존재 여부 확인
			if (applicantRepository.existsByStudentId(request.getStudentId())) {
				log.warn("이미 신청한 학번입니다 - studentId: {}", request.getStudentId());
				throw new RuntimeException("이미 신청한 학번입니다.");
			}

			// 3. 신청자 등록
			Applicant newApplicant = Applicant.builder()
				.applicantName(request.getApplicantName())
				.studentId(request.getStudentId())
				.build();

			applicantRepository.save(newApplicant);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			log.error("신청자 등록 중 오류 발생 - studentId: {}",
				request.getStudentId(), e);
			throw new RuntimeException("신청자 등록 중 오류가 발생했습니다.", e);
		}

	}

	/**
	 * 인증키 유효성 검사
	 * @param requestKey 요청에서 받은 인증키
	 * @return 인증키가 유효하면 true, 그렇지 않으면 false
	 */
	private boolean isValidAuthenticationKey(String requestKey) {
		try {
			// DB에서 유일한 인증키 조회
			AuthenticationKey authKey = authenticationKeyRepository.findFirstByOrderByIdAsc();

			if (authKey == null) {
				log.warn("DB에 등록된 인증키가 없습니다.");
				return false;
			}

			return authKey.getKey().equals(requestKey);
		} catch (Exception e) {
			log.error("인증키 검증 중 오류 발생", e);
			return false;
		}
	}


	/**
	 * 인증키 갱신 (기존 키 검증 후 새로운 키로 업데이트)
	 * @param newKey 새로운 인증키
	 * @param oldKey 기존인증키 (검증용)
	 */
	public void updateAuthenticationKey(String newKey, String oldKey) {
		try {
			// 1. DB에서 현재 인증키 조회
			AuthenticationKey currentAuthKey = authenticationKeyRepository.findFirstByOrderByIdAsc();

			// 2. DB에 인증키가 없는 경우
			if (currentAuthKey == null) {
				log.warn("DB에 등록된 인증키가 없습니다.");
				throw new RuntimeException("DB에 등록된 인증키가 없습니다.");
			}

			// 3. 기존 인증키(oldKey) 검증
			if (!currentAuthKey.getKey().equals(oldKey)) {
				log.warn("기존 인증키가 일치하지 않습니다. - 입력된 oldKey: {}", oldKey);
				throw new RuntimeException("기존 인증키가 일치하지 않습니다.");
			}

			// 4. 새로운 키와 기존 키가 같은 경우 체크
			if (currentAuthKey.getKey().equals(newKey)) {
				log.warn("새로운 인증키가 기존 키와 동일합니다.");
				throw new RuntimeException("새로운 인증키가 기존 키와 동일합니다.");
			}

			// 5. 기존 키 삭제
			authenticationKeyRepository.delete(currentAuthKey);

			// 6. 새로운 인증키 생성 및 저장
			AuthenticationKey newAuthKey = new AuthenticationKey(newKey);
			authenticationKeyRepository.save(newAuthKey);

		} catch (RuntimeException e) {
			throw e; // 비즈니스 로직 예외는 그대로 전파
		}
		catch (Exception e) {
			log.error("인증키 갱신 중 오류 발생", e);
			throw new RuntimeException("인증키 갱신 중 오류가 발생했습니다.", e);
		}

	}


}
