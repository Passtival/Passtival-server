package com.passtival.backend.domain.matching.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.passtival.backend.domain.matching.model.entity.Matching;
import com.passtival.backend.domain.matching.model.entity.MatchingApplicant;
import com.passtival.backend.domain.matching.model.enums.Gender;
import com.passtival.backend.domain.matching.repository.MatchingApplicantRepository;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

	private final MatchingApplicantRepository matchingApplicantRepository;
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
			List<MatchingApplicant> selectedMales = selectMembersByGender(Gender.MALE, matchingCount);
			List<MatchingApplicant> selectedFemales = selectMembersByGender(Gender.FEMALE, matchingCount);

			// 4. 랜덤 매칭 실행
			List<Matching> matchings = performRandomMatching(selectedMales, selectedFemales, matchingCount);

			// 5. 매칭 결과 저장
			saveMatchingResults(matchings);

		} finally {
			// 트랜잭션 동기화가 없거나, 등록 전에 크래시한 경우를 대비한 안전장치
			if (!syncRegistered) {
				inProgress.set(false);
			}
		}
	}

	/**
	 * 매칭 정리 시간: 매일 오후 11시 59분 59초 (59 59 23 * * *)
	 * 당일 매칭 데이터 초기화 및 모든 신청자 상태 초기화
	 */
	@Scheduled(cron = "59 59 23 * * *", zone = "Asia/Seoul")
	@Transactional
	public void dailyCleanup() {
		// 1. 모든 매칭 신청자들의 신청 상태 초기화 (성공자/실패자 구분 없이 일괄 처리)
		resetAllApplications();

		// 2. 모든 매칭 결과 삭제
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
		return matchingApplicantRepository.countByAppliedTrueAndGender(gender);
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
	private List<MatchingApplicant> selectMembersByGender(Gender gender, int count) {
		Pageable pageable = PageRequest.of(0, count);
		return matchingApplicantRepository.findByAppliedTrueAndGenderOrderByAppliedAtAsc(gender, pageable);
	}

	/**
	 * 랜덤 매칭 수행
	 * @param selectedMales 선별된 남성 회원 목록
	 * @param selectedFemales 선별된 여성 회원 목록
	 * @param matchingCount 매칭 수
	 * @return 매칭 결과 목록
	 */
	private List<Matching> performRandomMatching(List<MatchingApplicant> selectedMales,
		List<MatchingApplicant> selectedFemales,
		int matchingCount) {
		try {
			// 랜덤 매칭을 위한 여성 목록 셔플
			Collections.shuffle(selectedFemales);

			List<Matching> matchings = new ArrayList<>();
			for (int i = 0; i < matchingCount; i++) {
				MatchingApplicant male = selectedMales.get(i);
				MatchingApplicant female = selectedFemales.get(i);

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
	 * 모든 신청자의 신청 상태 초기화
	 */
	private void resetAllApplications() {
		matchingApplicantRepository.resetAllApplications();
	}

	/**
	 * 모든 매칭 결과 삭제
	 */
	private void deleteAllMatchingResults() {
		matchingRepository.deleteAllMatchingResults();
	}
}