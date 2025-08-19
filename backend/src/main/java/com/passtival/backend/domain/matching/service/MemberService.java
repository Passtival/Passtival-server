package com.passtival.backend.domain.matching.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.model.request.MemberOnboardingRequest;
import com.passtival.backend.domain.matching.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원 등록 (소셜 로그인 이후 추가 정보 입력)
	 * @param memberId 회원 ID
	 * @param memberOnboardingRequest 회원 등록 요청 정보
	 * @throws BaseException 회원 등록 실패 시
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void completeOnboarding(Long memberId, MemberOnboardingRequest memberOnboardingRequest) throws
		BaseException {
		try {
			Member newMember = getMemberById(memberId);

			// 이미 온보딩 완료 사용자인지 확인
			if (newMember.isOnboardingCompleted()) {
				throw new BaseException(BaseResponseStatus.ONBOARDING_ALREADY_COMPLETED);
			}

			// 전화번호 중복 검사
			String phoneNumber = parsePhoneNumber(memberOnboardingRequest.getPhoneNumber());

			// 회원 정보 생성 및 저장
			newMember.completeOnboarding(memberOnboardingRequest.getGender(), phoneNumber,
				memberOnboardingRequest.getInstagramId());

			memberRepository.save(newMember);

		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (DataIntegrityViolationException e) {
			//DB unique 제약조건 위반 시 처리
			throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
		} catch (Exception e) {
			log.error("회원 등록 중 예외 발생: {}", e.getMessage(), e);
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 전화번호 중복 검사
	 * @param phoneNumber 검사할 전화번호
	 * @throws BaseException 전화번호가 이미 사용 중인 경우
	 */
	private void validatePhoneNumber(String phoneNumber) throws BaseException {
		try {
			if (memberRepository.existsByPhoneNumber(phoneNumber)) {
				throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
			}
		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			log.error("전화번호 중복 검사 중 예외 발생: {}", e.getMessage(), e);
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// private void validateSocialId(String socialId) throws BaseException {
	// 	try {
	// 		if (memberRepository.existsBySocialId(socialId)) {
	// 			throw new BaseException(BaseResponseStatus.DUPLICATE_SOCIAL_ID);
	// 		}
	// 	}catch (BaseException e) {
	// 		throw e;
	// 	}catch (Exception e) {
	// 		log.error("전화번호 중복 검사 중 예외 발생: {}", e.getMessage(), e);
	// 		throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// }

	/**
	 * 회원 ID로 회원 조회
	 * @param memberId 회원 ID
	 * @return 회원 정보
	 * @throws BaseException 회원을 찾을 수 없는 경우
	 */
	public Member getMemberById(Long memberId) throws BaseException {
		try {
			return memberRepository.findById(memberId)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			log.error("회원 조회 중 예외 발생: memberId={}, error={}", memberId, e.getMessage(), e);
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 전화번호로 회원 조회 (화장성 고려) 사용하지 않기 때문에 주석처리
	 * @param phoneNumber 전화번호
	 * @return 회원 정보
	 * @throws BaseException 회원을 찾을 수 없는 경우
	 */
	// public Member getMemberByPhoneNumber(String phoneNumber) throws BaseException {
	// 	try {
	// 		return memberRepository.findByPhoneNumber(phoneNumber)
	// 			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
	// 	} catch (BaseException e) {
	// 		throw e; // BaseException은 그대로 전파
	// 	} catch (Exception e) {
	// 		log.error("전화번호로 회원 조회 중 예외 발생: phoneNumber={}, error={}",
	// 			phoneNumber, e.getMessage(), e);
	// 		throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// }

	/**
	 * 전화번호 정규화 및 중복 검사
	 * 허용 형식: "010-1234-5678", "010 1234 5678", "01012345678"
	 *
	 * @param phoneNumber 검사할 전화번호
	 * @throws BaseException 전화번호 형식이 올바르지 않거나 이미 사용 중인 경우
	 */
	private String parsePhoneNumber(String phoneNumber) throws BaseException {
		try {
			if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
				throw new BaseException(BaseResponseStatus.INVALID_PHONE_NUMBER_FORMAT);
			}

			String trimmed = phoneNumber.trim();
			String normalized;

			// 허용되는 3가지 형태 확인 및 정규화
			if (trimmed.matches("^010-\\d{4}-\\d{4}$")) {
				// "010-1234-5678" 형태
				normalized = trimmed.replaceAll("-", "");
			} else if (trimmed.matches("^010 \\d{4} \\d{4}$")) {
				// "010 1234 5678" 형태
				normalized = trimmed.replaceAll(" ", "");
			} else if (trimmed.matches("^010\\d{8}$")) {
				// "01012345678" 형태
				normalized = trimmed;
			} else {
				// 허용되지 않는 형태
				throw new BaseException(BaseResponseStatus.INVALID_PHONE_NUMBER_FORMAT);
			}

			// 중복 검사 (정규화된 번호로)
			if (memberRepository.existsByPhoneNumber(normalized)) {
				throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
			}
			return normalized;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			log.error("전화번호 검증 중 예외 발생: phoneNumber={}, error={}", phoneNumber, e.getMessage(), e);
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
}