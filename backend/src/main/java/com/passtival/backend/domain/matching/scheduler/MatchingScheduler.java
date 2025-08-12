package com.passtival.backend.domain.matching.scheduler;

import com.passtival.backend.domain.matching.entity.Matching;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.domain.member.enums.Gender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingScheduler {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;

    // 매칭 진행 상태 플래그 추가
    private volatile boolean isMatchingInProgress = false;

    /** 매칭 시작 시간: 매일 오후 6시 1분 0 1 18 * * *
     * 매칭중에 신청 방지 되어있음
     */
    @Scheduled(cron = "0 1 18 * * *")
    @Transactional
    public void executeMatching() {
        // 여기에 동시성 제어 로직 추가 필요

        //매칭중에 신청 방지
        if (isMatchingInProgress) {
            log.warn("매칭이 이미 진행 중입니다. 이번 스케줄을 건너뜁니다.");
            return;
        }

        try {
            isMatchingInProgress = true;
            long maleCount = memberRepository.countByAppliedTrueAndGender(Gender.MALE);
            long femaleCount = memberRepository.countByAppliedTrueAndGender(Gender.FEMALE);


            // 매칭 수 결정 + (최대 1000쌍) 제한 적용
            int matchingCount = (int) Math.min(Math.min(maleCount, femaleCount), 1000);

            if (matchingCount == 0) {
                log.info("매칭 가능한 사용자가 없습니다.");
                memberRepository.resetAllApplications();
                return;
            }

            Pageable malePageable = PageRequest.of(0, matchingCount);
            Pageable femalePageable = PageRequest.of(0, matchingCount);

            List<Member> selectedMales = memberRepository
                    .findTopApplicantsByGender(Gender.MALE, malePageable);
            List<Member> selectedFemales = memberRepository
                    .findTopApplicantsByGender(Gender.FEMALE, femalePageable);

            // 랜덤 매칭
            Collections.shuffle(selectedFemales);

            // 배치 저장으로 성능 개선
            List<Matching> matchings = new ArrayList<>();

            for (int i = 0; i < matchingCount; i++) {
                Member male = selectedMales.get(i);
                Member female = selectedFemales.get(i);

                Matching result = Matching.createMatching(
                        male.getMemberId(),
                        female.getMemberId()
                );

                matchings.add(result);
            }

            // 배치로 한 번에 저장
            matchingRepository.saveAll(matchings);

            // 메모리 효율적인 실패자 처리
            handleFailedApplicantsEfficiently(maleCount, femaleCount, selectedMales, selectedFemales);

        } catch (Exception e) {
            log.error("매칭 알고리즘 실행 중 오류: ", e);
            throw e;
        } finally {
            isMatchingInProgress = false;
        }
    }

    /** 매칭 시작 시간: 매일 오후 11시 59분에 진행: 0 59 23 * * *
     * 매칭중에 신청 방지 되어있음
     */
    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void dailyCleanup() {

        // 당일 매칭 성공자들 조회
        List<Matching> todayResults = matchingRepository.findByMatchingDate(LocalDate.now(ZoneId.of("Asia/Seoul")));

        if (!todayResults.isEmpty()) {
            // 매칭 성공자들의 memberId 수집
            List<Long> matchedMemberIds = new ArrayList<>();

            for (Matching result : todayResults) {
                matchedMemberIds.add(result.getMaleId());
                matchedMemberIds.add(result.getFemaleId());
            }

            // 매칭 성공자들의 신청 상태 초기화
            memberRepository.resetApplicationsByMemberIds(matchedMemberIds);
        }

        //모든 매칭 결과 삭제
        matchingRepository.deleteAllMatchingResults();
    }

    // 매칭 중인지 검토하는 로직 (서비스에서 신청 제한에서 사용)
    public boolean isMatchingInProgress() {
        return isMatchingInProgress;
    }

    //실패자만 별도로 처리하는 과정을 미리 분리하여 최적화
    private void handleFailedApplicantsEfficiently(long maleCount, long femaleCount,
                                                   List<Member> selectedMales,
                                                   List<Member> selectedFemales) {

        // 모든 신청자가 매칭됨 - 실패자 처리 생략
        if (selectedMales.size() == maleCount && selectedFemales.size() == femaleCount) {
            return;
        }

        // 성공자 ID만 수집
        Set<Long> matchedIds = new HashSet<>();
        selectedMales.forEach(member -> matchedIds.add(member.getMemberId()));
        selectedFemales.forEach(member -> matchedIds.add(member.getMemberId()));

        // 실패자만 선별적으로 ID만으로 처리
        if (!matchedIds.isEmpty()) {
            memberRepository.resetApplicationsForUnmatched(matchedIds);
        } else {
            // NOT IN이 비어있는 Set을 처리하지 못할 수 있으므로 모든 신청자를 초기화하는 별도 처리
            memberRepository.resetAllApplications();
        }

    }
}
