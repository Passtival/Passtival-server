package com.passtival.backend.domain.matching.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.passtival.backend.domain.matching.model.entity.Matching;
import com.passtival.backend.domain.matching.model.entity.Member;
import com.passtival.backend.domain.matching.model.enums.Gender;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.domain.matching.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

	private final MemberRepository memberRepository;
	private final MatchingRepository matchingRepository;

	//최대 1000쌍 제한
	private static final int MAX_MATCHING_PAIRS = 1000;
	// 매칭 진행 상태 플래그 추가
	private final AtomicBoolean inProgress = new AtomicBoolean(false);

	/**
	 * 매칭 시작 시간: 매일 오후 6시 0분 (0 0 18 * * *)
	 * 매칭중에 신청 방지 되어있음
	 * (rollbackFor = Exception.class)을 통해 매칭 문제 발생시 롤백
	 */
	@Transactional(rollbackFor = Exception.class)
	@Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
	public void dailyMatching() {
		if (!inProgress.compareAndSet(false, true)) {
			return;
		}

		//반환의 우선 순위 때문에 매칭 도중 사용될 가능성 방지
		boolean syncRegistered = false;

		try {
			// 트랜잭션 동기화가 켜져 있으면 커밋/롤백 이후에 플래그 내리기
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCompletion(int status) {
						inProgress.set(false);
					}
				});
				syncRegistered = true;
			}

			// 이미 매칭이 진행 중이면 종료
			if (matchingRepository.existsByMatchingDate(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
				return;
			}

			// 1. 매칭 가능 인원 확인
			long maleCount = countApplicantsByGender(Gender.MALE);
			long femaleCount = countApplicantsByGender(Gender.FEMALE);

			// 2. 매칭 수 결정
			int matchingCount = calculateMatchingCount(maleCount, femaleCount);

			if (matchingCount <= 0) {
				resetAllApplications();
				return;
			}

			// 3. 매칭 대상자 선별
			List<Member> selectedMales = selectMembersByGender(Gender.MALE, matchingCount);
			List<Member> selectedFemales = selectMembersByGender(Gender.FEMALE, matchingCount);

			// 4. 랜덤 매칭 실행
			List<Matching> matchings = performRandomMatching(selectedMales, selectedFemales, matchingCount);

			// 5. 매칭 결과 저장
			saveMatchingResults(matchings);

			// 6. 매칭 실패자 처리
			handleFailedApplicants(maleCount, femaleCount, selectedMales.subList(0, matchingCount),
				selectedFemales.subList(0, matchingCount));
		} finally {
			// 트랜잭션 동기화가 없거나, 등록 전에 크래시한 경우를 대비한 안전장치
			if (!syncRegistered) {
				inProgress.set(false);
			}
		}
	}

	/**
	 * 매칭 정리 시간: 매일 오후 11시 59분 (0 59 23 * * *)
	 * 당일 매칭 데이터 초기화
	 */
	@Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
	@Transactional
	public void dailyCleanup() {
		// 1. 당일 매칭 성공자 조회
		List<Matching> todayResults = getTodayMatchingResults();

		if (!todayResults.isEmpty()) {
			// 2. 매칭 성공자들의 신청 상태 초기화
			List<Long> matchedMemberIds = extractMatchedMemberIds(todayResults);
			resetApplicationsForMatchedMembers(matchedMemberIds);
		}

		// 3. 모든 매칭 결과 삭제
		deleteAllMatchingResults();

	}

	/**
	 * 매칭 진행 상태 확인
	 * @return 매칭 진행 중 여부
	 */
	public boolean isMatchingInProgress() {
		return inProgress.get();
	}

	/**
	 * 성별별 신청자 수 조회
	 * @param gender 성별
	 * @return 신청자 수
	 */
	private long countApplicantsByGender(Gender gender) {
		return memberRepository.countByAppliedTrueAndGender(gender);
	}

	/**
	 * 매칭 수 계산
	 * @param maleCount 남성 신청자 수
	 * @param femaleCount 여성 신청자 수
	 * @return 매칭 가능 수 (최대 1000쌍)
	 */
	private int calculateMatchingCount(long maleCount, long femaleCount) {
		int matchingCount = (int)Math.min(Math.min(maleCount, femaleCount), MAX_MATCHING_PAIRS);
		return matchingCount;
	}

	/**
	 * 성별별 매칭 대상자 선별 (선착순)
	 * @param gender 성별
	 * @param count 선별할 인원 수
	 * @return 선별된 회원 목록
	 */
	private List<Member> selectMembersByGender(Gender gender, int count) {
		Pageable pageable = PageRequest.of(0, count);
		return memberRepository.findByAppliedTrueAndGenderOrderByAppliedAtAsc(gender, pageable);
	}

	/**
	 * 랜덤 매칭 수행
	 * @param selectedMales 선별된 남성 회원 목록
	 * @param selectedFemales 선별된 여성 회원 목록
	 * @param matchingCount 매칭 수
	 * @return 매칭 결과 목록
	 */
	private List<Matching> performRandomMatching(List<Member> selectedMales, List<Member> selectedFemales,
		int matchingCount) {
		try {
			// 랜덤 매칭을 위한 여성 목록 셔플
			Collections.shuffle(selectedFemales);

			List<Matching> matchings = new ArrayList<>();
			for (int i = 0; i < matchingCount; i++) {
				Member male = selectedMales.get(i);
				Member female = selectedFemales.get(i);

				Matching matching = Matching.createMatching(male.getMemberId(), female.getMemberId());
				matchings.add(matching);
			}

			return matchings;

		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 매칭 결과 저장
	 * @param matchings 매칭 결과 목록
	 */
	private void saveMatchingResults(List<Matching> matchings) {
		try {
			matchingRepository.saveAll(matchings);
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
		}
	}

	/**
	 * 매칭 실패자 처리
	 * @param maleCount 전체 남성 신청자 수
	 * @param femaleCount 전체 여성 신청자 수
	 * @param selectedMales 매칭된 남성 목록
	 * @param selectedFemales 매칭된 여성 목록
	 */
	private void handleFailedApplicants(long maleCount, long femaleCount,
		List<Member> selectedMales, List<Member> selectedFemales) {
		// 모든 신청자가 매칭된 경우 - 실패자 처리 생략
		if (selectedMales.size() == maleCount && selectedFemales.size() == femaleCount) {
			return;
		}

		// 매칭 성공자 ID 수집
		Set<Long> matchedIds = new HashSet<>();
		selectedMales.forEach(member -> matchedIds.add(member.getMemberId()));
		selectedFemales.forEach(member -> matchedIds.add(member.getMemberId()));

		long totalApplicants = maleCount + femaleCount;
		long failedCount = totalApplicants - matchedIds.size();

		// 매칭 실패자들의 신청 상태 초기화
		if (matchedIds.isEmpty()) {
			// 매칭된 회원이 없는 경우 모든 신청자 초기화
			memberRepository.resetAllApplications();
		} else if (failedCount > 0) {
			// 매칭 실패자만 초기화 (matchedIds가 비어있지 않을 때만 실행)
			memberRepository.resetApplicationsForUnmatched(matchedIds);
		}

	}

	/**
	 * 모든 신청자의 신청 상태 초기화
	 */
	private void resetAllApplications() {
		memberRepository.resetAllApplications();
	}

	/**
	 * 당일 매칭 결과 조회
	 * @return 당일 매칭 결과 목록
	 */
	private List<Matching> getTodayMatchingResults() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		return matchingRepository.findByMatchingDate(today);
	}

	/**
	 * 매칭 결과에서 회원 ID 목록 추출
	 * @param matchingResults 매칭 결과 목록
	 * @return 매칭된 회원 ID 목록
	 */
	private List<Long> extractMatchedMemberIds(List<Matching> matchingResults) {
		List<Long> matchedMemberIds = new ArrayList<>();
		for (Matching result : matchingResults) {
			matchedMemberIds.add(result.getMaleId());
			matchedMemberIds.add(result.getFemaleId());
		}
		return matchedMemberIds;

	}

	/**
	 * 매칭 성공자들의 신청 상태 초기화
	 * @param matchedMemberIds 매칭된 회원 ID 목록
	 */
	private void resetApplicationsForMatchedMembers(List<Long> matchedMemberIds) {
		memberRepository.resetApplicationsByMemberIds(matchedMemberIds);
	}

	/**
	 * 모든 매칭 결과 삭제
	 */
	private void deleteAllMatchingResults() {
		matchingRepository.deleteAllMatchingResults();
	}
}