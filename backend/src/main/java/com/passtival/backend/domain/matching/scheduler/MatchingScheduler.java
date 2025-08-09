package com.passtival.backend.domain.matching.scheduler;

import com.passtival.backend.domain.matching.entity.MatchingResult;
import com.passtival.backend.domain.matching.repository.MatchingResultRepository;
import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.domain.user.repository.UserRepository;
import com.passtival.backend.domain.user.enums.Gender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingScheduler {

    private final UserRepository userRepository;
    private final MatchingResultRepository  matchingResultRepository;

    // 매칭 진행 상태 플래그 추가
    private volatile boolean isMatchingInProgress = false;

    // 테스트용: 매 1분마다 실행 (개발 시에만 사용)
    // @Scheduled(cron = "0 */1 * * * *")
    // 운영용: 매일 오후 6시 정시 0 0 18 * * *
    @Scheduled(cron = "0 1 18 * * *")
    @Transactional
    public void executeMatching() {
        // 여기에 동시성 제어 로직 추가 필요 매칭 중에는 데이터 저장 방지

        if (isMatchingInProgress) {
            log.warn("매칭이 이미 진행 중입니다. 이번 스케줄을 건너뜁니다.");
            return;
        }

        try {
            isMatchingInProgress = true;
            log.info("=== 매칭 알고리즘 시작 ===");
            long maleCount = userRepository.countByIsApplyTrueAndGender(Gender.male);
            long femaleCount = userRepository.countByIsApplyTrueAndGender(Gender.female);

            log.info("신청자 현황 - 남성: {}명, 여성: {}명", maleCount, femaleCount);

            // 매칭 수 결정 + 최대 제한 적용
            int matchingCount = (int) Math.min(Math.min(maleCount, femaleCount), 1000); // 최대 1000쌍

            if (matchingCount == 0) {
                log.info("매칭 가능한 사용자가 없습니다.");
                userRepository.resetAllApplications();
                return;
            }

            Pageable malePageable = PageRequest.of(0, matchingCount);
            Pageable femalePageable = PageRequest.of(0, matchingCount);

            List<User> selectedMales = userRepository
                    .findTopApplicantsByGender(Gender.male, malePageable);
            List<User> selectedFemales = userRepository
                    .findTopApplicantsByGender(Gender.female, femalePageable);

            // 랜덤 매칭
            Collections.shuffle(selectedFemales);

            // 🔄 변경: 배치 저장으로 성능 개선
            List<MatchingResult> matchingResults = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (int i = 0; i < matchingCount; i++) {
                User male = selectedMales.get(i);
                User female = selectedFemales.get(i);

                MatchingResult result = new MatchingResult();
                result.setUserId1(male.getUserId());
                result.setUserId2(female.getUserId());
                result.setMatchingDate(today);

                matchingResults.add(result);

                log.info("매칭 준비: {} (남성) ↔ {} (여성)",
                        male.getName(), female.getName());
            }

            // 배치로 한 번에 저장
            matchingResultRepository.saveAll(matchingResults);

            // 🔄 변경: 메모리 효율적인 실패자 처리
            handleFailedApplicantsEfficiently(maleCount, femaleCount, selectedMales, selectedFemales);

            log.info("=== 매칭 완료 - {}쌍 매칭 ===", matchingCount);

        } catch (Exception e) {
            log.error("매칭 알고리즘 실행 중 오류: ", e);
            throw e;
        } finally {
            isMatchingInProgress = false;
        }
    }

    @Scheduled(cron = "0 59 23 * * *") // 매일 23:59
    @Transactional
    public void dailyCleanup() {
        log.info("=== 일일 데이터 정리 시작 ===");

        // 1. 당일 매칭 성공자들 조회
        List<MatchingResult> todayResults = matchingResultRepository.findByMatchingDate(LocalDate.now());

        if (!todayResults.isEmpty()) {
            // 2. 매칭 성공자들의 userId 수집
            List<Long> matchedUserIds = new ArrayList<>();
            for (MatchingResult result : todayResults) {
                matchedUserIds.add(result.getUserId1());
                matchedUserIds.add(result.getUserId2());
            }

            // 3. 매칭 성공자들의 신청 상태 초기화
            userRepository.resetApplicationsByUserIds(matchedUserIds);
            log.info("매칭 성공자 {}명의 신청 상태 초기화 완료", matchedUserIds.size());
        }

// 4. 모든 매칭 결과 삭제
        matchingResultRepository.deleteAllMatchingResults();
        log.info("모든 매칭 결과 삭제 완료");
    }

    public boolean isMatchingInProgress() {
        return isMatchingInProgress;
    }

    private void handleFailedApplicantsEfficiently(long maleCount, long femaleCount,
                                                   List<User> selectedMales,
                                                   List<User> selectedFemales) {

        // 모든 신청자가 매칭되었다면 실패자 없음
        if (selectedMales.size() == maleCount && selectedFemales.size() == femaleCount) {
            log.info("모든 신청자가 매칭됨 - 실패자 처리 생략");
            return;
        }

        // 🔄 개선: 성공자 ID만 수집 (메모리 효율적)
        Set<Long> matchedIds = new HashSet<>();
        selectedMales.forEach(user -> matchedIds.add(user.getUserId()));
        selectedFemales.forEach(user -> matchedIds.add(user.getUserId()));

        // 🔄 개선: 실패자만 선별적으로 처리
        // 전체를 로드하지 않고 ID만으로 처리
        List<Long> allMaleIds = userRepository
                .findUserIdsByGender(Gender.male);
        List<Long> allFemaleIds = userRepository
                .findUserIdsByGender(Gender.female);

        List<Long> failedIds = new ArrayList<>();

        allMaleIds.stream()
                .filter(id -> !matchedIds.contains(id))
                .forEach(failedIds::add);

        allFemaleIds.stream()
                .filter(id -> !matchedIds.contains(id))
                .forEach(failedIds::add);

        if (!failedIds.isEmpty()) {
            userRepository.resetApplicationsByUserIds(failedIds);
            log.info("매칭 실패자 {}명의 신청 상태 초기화 완료", failedIds.size());
        }
    }
}
