package com.passtival.backend.domain.matching.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.model.request.MemberPatchRequest;
import com.passtival.backend.domain.matching.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원 등록 (소셜 로그인 이후 추가 정보 입력)
	 * @param memberId 회원 ID
	 * @param request 회원 등록 요청 정보
	 * @throws BaseException 회원 등록 실패 시
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void patchProfile(Long memberId, MemberPatchRequest request) throws BaseException {
		try {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

			// 1. 사전 검증 - 모든 변경사항을 먼저 검증
			validateAllChanges(member, request);

			// 2. 검증 통과 후 일괄 업데이트
			applyUpdates(member, request);

			// 3. 최종 상태 검증
			validateFinalState(member);

		} catch (BaseException e) {
			throw e;
		} catch (DataIntegrityViolationException e) {
			throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 모든 변경사항을 사전 검증
	private void validateAllChanges(Member member, MemberPatchRequest request) throws BaseException {
		// 전화번호 검증
		if (request.getPhoneNumber().isPresent()) {
			String newPhoneNumber = request.getPhoneNumber().get();
			String NormalizedPhoneNumber = newPhoneNumber.trim().isEmpty() ? null : newPhoneNumber;
			if (!NormalizedPhoneNumber.equals(member.getPhoneNumber())) {
				validatePhoneNumber(newPhoneNumber, member.getMemberId());
			}
		}

		// 인스타그램 ID 검증
		if (request.getInstagramId().isPresent()) {
			String newInstagramId = request.getInstagramId().get();
			String NormalizedInstagramId = newInstagramId.trim().isEmpty() ? null : newInstagramId;
			if (!NormalizedInstagramId.equals(member.getInstagramId())) {
				validateInstagramId(newInstagramId, member.getMemberId());
			}
		}
	}

	//검증 완료 후 실제 업데이트 적용
	private void applyUpdates(Member member, MemberPatchRequest request) {
		// 전화번호 업데이트
		if (request.getPhoneNumber().isPresent()) {
			member.updatePhoneNumber(request.getPhoneNumber().get());
		}

		// 인스타그램 ID 업데이트
		if (request.getInstagramId().isPresent()) {
			member.updateInstagramId(request.getInstagramId().get());
		}

		// 성별 업데이트 (중복 검사 불필요하므로 바로 적용)
		if (request.getGender().isPresent()) {
			member.updateGender(request.getGender().get());
		}
	}

	//성별 있는가? 번호 혹은 인스타 id가 있는가?
	private void validateFinalState(Member member) throws BaseException {

		if (member.getGender() == null) {
			throw new BaseException(BaseResponseStatus.GENDER_REQUIRED);
		}
		if (isContactInfoEmpty(member)) {
			throw new BaseException(BaseResponseStatus.CONTACT_INFO_REQUIRED);
		}
	}

	/**
	 * 전화번호 중복 검사 (자기 자신 제외)
	 * @param phoneNumber 검사할 전화번호
	 * @param currentMemberId 현재 회원 ID
	 * @throws BaseException 전화번호가 이미 사용 중인 경우
	 */
	private void validatePhoneNumber(String phoneNumber, Long currentMemberId) throws BaseException {
		if (phoneNumber != null && !phoneNumber.isEmpty() &&
			memberRepository.existsByPhoneNumberAndMemberIdNot(phoneNumber, currentMemberId)) {
			throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
		}
	}

	/**
	 * 인스타그램 ID 중복 검사 (자기 자신 제외)
	 * @param instagramId 검증할 인스타그램 ID
	 * @param currentMemberId 현재 회원 ID
	 * @throws BaseException 중복 시 예외 발생
	 */
	private void validateInstagramId(String instagramId, Long currentMemberId) throws BaseException {
		if (instagramId != null && !instagramId.isEmpty() &&
			memberRepository.existsByInstagramIdAndMemberIdNot(instagramId, currentMemberId)) {
			throw new BaseException(BaseResponseStatus.DUPLICATE_INSTAGRAM_ID);
		}
	}

	/**
	 * Member 객체의 연락처 정보가 모두 비어있는지 확인합니다.
	 * @param member 검사할 Member 객체
	 * @return 연락처가 모두 비어있으면 true
	 */
	private boolean isContactInfoEmpty(Member member) {
		String phone = member.getPhoneNumber();
		String instagram = member.getInstagramId();
		return (phone == null || phone.trim().isEmpty()) && (instagram == null || instagram.trim().isEmpty());
	}

	/**
	 * 회원 ID로 회원 조회
	 * @param memberId 회원 ID
	 * @return 회원 정보
	 * @throws BaseException 회원을 찾을 수 없는 경우
	 */
	public Member getMemberById(Long memberId) throws BaseException {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
	}
}