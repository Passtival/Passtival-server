package com.passtival.backend.domain.matching.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.matching.model.entity.Matching;
import com.passtival.backend.domain.matching.model.entity.MatchingApplicant;
import com.passtival.backend.domain.matching.model.response.MatchingResponse;
import com.passtival.backend.domain.matching.repository.MatchingApplicantRepository;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

/**
 * 매칭 관련 비즈니스 로직을 처리하는 서비스
 * 회원가입, 사용자 검증, 데이터 안전성 보장을 담당
 */
@Service
@RequiredArgsConstructor
public class MatchingService {

	private final MatchingApplicantRepository matchingApplicantRepository;
	private final MatchingRepository matchingRepository;
	private final MatchingScheduler matchingScheduler;

	@Value("${MATCHING_BLOCK}")
	private String matchingBlockCron;

	@Transactional
	public void applyMatching(Long memberId) {

		if (matchingScheduler.isMatchingInProgress()) {
			throw new BaseException(BaseResponseStatus.MATCHING_IN_PROGRESS);
		}

		LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
		LocalTime endTime = parseEndTimeFromCron(matchingBlockCron);

		// 신청 가능 시간: 00:00 ~ 17:30
		if (now.isAfter(endTime)) {
			throw new BaseException(BaseResponseStatus.MATCHING_TIME_INVALID);
		}

		// 사용자 존재 여부 확인
		MatchingApplicant matchingApplicant = matchingApplicantRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

		if (matchingApplicant.getGender() == null) {
			throw new BaseException(BaseResponseStatus.INCOMPLETE_MEMBER_INFO);
		}

		// 중복 신청 검증
		if (matchingApplicant.isApplied()) {
			throw new BaseException(BaseResponseStatus.ALREADY_APPLIED_MATCHING);
		}

		matchingApplicant.applyForMatching();

		matchingApplicantRepository.save(matchingApplicant);
	}

	private LocalTime parseEndTimeFromCron(String cronExpression) {
		try {
			String[] parts = cronExpression.trim().split("\\s+");

			if (parts.length < 3) {
				throw new BaseException(BaseResponseStatus.CRON_ERROR);
			}

			int minute = Integer.parseInt(parts[1]);
			int hour = Integer.parseInt(parts[2]);

			// 시간 유효성 검증
			if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
				throw new BaseException(BaseResponseStatus.CRON_ERROR);
			}

			return LocalTime.of(hour, minute);

		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			// 파싱 실패 시 기본값 사용 (17:30)
			return LocalTime.of(17, 30);
		}
	}

	public MatchingResponse getMatchingResult(Long memberId) {
		// 오늘 날짜의 매칭 결과 조회
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		Optional<Matching> matchingResult = matchingRepository
			.findMemberMatchingByDate(today, memberId);

		if (matchingResult.isEmpty()) {
			throw new BaseException(BaseResponseStatus.MATCHING_RESULT_NOT_FOUND);
		}

		Matching matching = matchingResult.get();

		// 나의 memberId
		Long myMemberId = memberId;

		// 파트너의 memberId
		Long partnerMemberId = matching.getMaleId().equals(myMemberId)
			? matching.getFemaleId() : matching.getMaleId();

		// 사용자(나와 파트너) 정보 조회
		List<MatchingApplicant> matchingApplicants = matchingApplicantRepository
			.findByMemberIdIn(Arrays.asList(myMemberId, partnerMemberId));

		if (matchingApplicants.size() != 2) {
			throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
		}

		// 내 정보와 상대방 정보 분리
		MatchingApplicant myMatchingApplicant = matchingApplicants.stream()
			.filter(member -> member.getMemberId().equals(myMemberId))
			.findFirst()
			.orElse(null);

		MatchingApplicant partnerMatchingApplicant = matchingApplicants.stream()
			.filter(member -> member.getMemberId().equals(partnerMemberId))
			.findFirst()
			.orElse(null);

		if (myMatchingApplicant == null || partnerMatchingApplicant == null) {
			throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
		}

		// DTO 생성
		MatchingResponse.MemberInfo myInfo = MatchingResponse.MemberInfo.builder()
			.phoneNumber(myMatchingApplicant.getPhoneNumber())
			.instagramId(myMatchingApplicant.getInstagramId())
			.build();

		MatchingResponse.MemberInfo partnerInfo = MatchingResponse.MemberInfo.builder()
			.phoneNumber(partnerMatchingApplicant.getPhoneNumber())
			.instagramId(partnerMatchingApplicant.getInstagramId())
			.build();

		MatchingResponse response = MatchingResponse.builder()
			.myInfo(myInfo)
			.partnerInfo(partnerInfo)
			.matchingDate(today.toString())
			.build();

		return response;
	}
}
