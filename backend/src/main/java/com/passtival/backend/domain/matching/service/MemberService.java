package com.passtival.backend.domain.matching.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.model.request.MemberPatchRequest;
import com.passtival.backend.domain.matching.model.response.MemberResponse;
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
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void patchProfile(Long memberId, MemberPatchRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

		// 1. 사전 검증 - 모든 변경사항을 먼저 검증
		validateAllChanges(member, request);

		// 2. 검증 통과 후 일괄 업데이트
		applyUpdates(member, request);

		// 3. 최종 상태 검증
		validateFinalState(member);

	}

	// 모든 변경사항을 사전 검증
	private void validateAllChanges(Member member, MemberPatchRequest request) {
		// 전화번호 검증 (null이 아닌 경우만 - null은 수정하지 않음을 의미)
		if (request.getPhoneNumber() != null) {
			String newPhoneNumber = request.getPhoneNumber();
			String normalizedPhoneNumber = newPhoneNumber.trim().isEmpty() ? null : newPhoneNumber;
			if (!java.util.Objects.equals(normalizedPhoneNumber, member.getPhoneNumber())) {
				validatePhoneNumber(newPhoneNumber, member.getMemberId());
			}
		}

		// 인스타그램 ID 검증 (null이 아닌 경우만 - null은 수정하지 않음을 의미)
		if (request.getInstagramId() != null) {
			String newInstagramId = request.getInstagramId();
			String normalizedInstagramId = newInstagramId.trim().isEmpty() ? null : newInstagramId;
			if (!java.util.Objects.equals(normalizedInstagramId, member.getInstagramId())) {
				validateInstagramId(newInstagramId, member.getMemberId());
			}
		}
	}

	//검증 완료 후 실제 업데이트 적용
	private void applyUpdates(Member member, MemberPatchRequest request) {
		// 전화번호 업데이트 (null이 아닌 경우만)
		if (request.getPhoneNumber() != null) {
			member.updatePhoneNumber(request.getPhoneNumber());
		}

		// 인스타그램 ID 업데이트 (null이 아닌 경우만)
		if (request.getInstagramId() != null) {
			member.updateInstagramId(request.getInstagramId());
		}

		// 성별 업데이트 (null이 아닌 경우만 - 중복 검사 불필요하므로 바로 적용)
		if (request.getGender() != null) {
			member.updateGender(request.getGender());
		}
	}

	// 성별 있는가? 번호 혹은 인스타 id가 있는가?
	private void validateFinalState(Member member) {

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
	 */
	private void validatePhoneNumber(String phoneNumber, Long currentMemberId) {
		if (phoneNumber != null && !phoneNumber.isEmpty() &&
			memberRepository.existsByPhoneNumberAndMemberIdNot(phoneNumber, currentMemberId)) {
			throw new BaseException(BaseResponseStatus.DUPLICATE_PHONE_NUMBER);
		}
	}

	/**
	 * 인스타그램 ID 중복 검사 (자기 자신 제외)
	 * @param instagramId 검증할 인스타그램 ID
	 * @param currentMemberId 현재 회원 ID
	 */
	private void validateInstagramId(String instagramId, Long currentMemberId) {
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
	 */
	public Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
	}

	/**
	 * 회원 프로필 조회
	 * @param memberId 회원 ID
	 * @return 회원 프로필 정보
	 */
	public MemberResponse getProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

		return MemberResponse.builder()
			.MemberId(member.getMemberId())
			.MemberName(member.getName())
			.MemberGender(member.getGender())
			.MemberPhoneNumber(member.getPhoneNumber())
			.MemberInstagramId(member.getInstagramId())
			.build();

	}
}