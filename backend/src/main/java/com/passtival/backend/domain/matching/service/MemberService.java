package com.passtival.backend.domain.matching.service;

import org.springframework.stereotype.Service;

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
	public void completeOnboarding(Long memberId, MemberOnboardingRequest memberOnboardingRequest) throws
		BaseException {
		try {
			Member newMember = getMemberById(memberId);

			// 이미 온보딩 완료 사용자인지 확인
			if (newMember.isOnboardingCompleted()) {
				throw new BaseException(BaseResponseStatus.ONBOARDING_ALREADY_COMPLETED);
			}

			// 전화번호 중복 검사
			validatePhoneNumber(memberOnboardingRequest.getPhoneNumber());

			// 회원 정보 생성 및 저장
			newMember.completeOnboarding(memberOnboardingRequest.getGender(), memberOnboardingRequest.getPhoneNumber());

			memberRepository.save(newMember);

		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
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
	 * 전화번호로 회원 조회
	 * @param phoneNumber 전화번호
	 * @return 회원 정보
	 * @throws BaseException 회원을 찾을 수 없는 경우
	 */
	public Member getMemberByPhoneNumber(String phoneNumber) throws BaseException {
		try {
			return memberRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			log.error("전화번호로 회원 조회 중 예외 발생: phoneNumber={}, error={}",
				phoneNumber, e.getMessage(), e);
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
}