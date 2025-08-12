package com.passtival.backend.domain.matching.service;

import com.passtival.backend.domain.matching.dto.MatchingApplyDto;
import com.passtival.backend.domain.matching.dto.MatchingDto;
import com.passtival.backend.domain.matching.entity.Matching;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.domain.matching.scheduler.MatchingScheduler;
import com.passtival.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 매칭 관련 비즈니스 로직을 처리하는 서비스
 * 회원가입, 사용자 검증, 데이터 안전성 보장을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final MatchingScheduler matchingScheduler;

    @Transactional
    public void applyMatching(Long memberId, MatchingApplyDto matchingApplyDto) throws BaseException {
        try {
            if (matchingScheduler.isMatchingInProgress()) {
                throw new BaseException(BaseResponseStatus.MATCHING_IN_PROGRESS);
            }

            LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
            LocalTime startTime = LocalTime.of(0, 0);   // 00:00
            LocalTime endTime = LocalTime.of(18, 0);    // 18:00

            // 신청 가능 시간: 00:00 ~ 18:00
            if (now.isBefore(startTime) || now.isAfter(endTime)) {
                throw new BaseException(BaseResponseStatus.MATCHING_TIME_INVALID);
            }

            // 사용자 존재 여부 확인
            Member member = memberRepository.findById(memberId)
                    .orElse(null);

            if (member == null) {
                log.warn("존재하지 않는 사용자: memberId = {}", memberId);
                throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
            }
            if(!member.isOnboardingCompleted()){
                throw new BaseException(BaseResponseStatus.ONBOARDING_REQUIRED);
            }

            // 중복 신청 검증
            if (member.isApplied()) {
                log.info("이미 매칭 신청한 사용자: memberId = {}", memberId);
                throw new BaseException(BaseResponseStatus.ALREADY_APPLIED_MATCHING);
            }

                member.applyForMatching(matchingApplyDto.getInstagramId());

                memberRepository.save(member);


        }catch (BaseException e) {
            throw e; // BaseException은 그대로 전파
        } catch (Exception e) {
            log.error("매칭 신청 중 예외 발생: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MatchingDto getMatchingResult(Long memberId) throws BaseException {
        try{
            // 오늘 날짜의 매칭 결과 조회
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

            Optional<Matching> matchingResultOpt = matchingRepository
                    .findMemberMatchingByDate(today, memberId);

            if (matchingResultOpt.isEmpty()) {
                throw new BaseException(BaseResponseStatus.MATCHING_RESULT_NOT_FOUND);
            }

            Matching matching = matchingResultOpt.get();

            // 나의 memberId
            Long myMemberId = memberId;

            // 파트너의 memberId
            Long partnerMemberId =
                    matching.getMaleId().equals(myMemberId) ? matching.getFemaleId() : matching.getMaleId();

            // 사용자(나와 파트너) 정보 조회
            List<Member> members = memberRepository
                    .findByMemberIdIn(Arrays.asList(myMemberId, partnerMemberId));

            if (members.size() != 2) {
                throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
            }

            // 내 정보와 상대방 정보 분리
            Member myMember = members.stream()
                    .filter(member -> member.getMemberId().equals(myMemberId))
                    .findFirst()
                    .orElse(null);

            Member partnerMember = members.stream()
                    .filter(member -> member.getMemberId().equals(partnerMemberId))
                    .findFirst()
                    .orElse(null);

            if (myMember == null || partnerMember == null) {
                throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
            }
            // DTO 생성
            MatchingDto.MemberInfo myInfo = MatchingDto.MemberInfo.builder()
                    .phoneNumber(myMember.getPhoneNumber())
                    .instagramId(myMember.getInstagramId())
                    .build();

            MatchingDto.MemberInfo partnerInfo = MatchingDto.MemberInfo.builder()
                    .phoneNumber(partnerMember.getPhoneNumber())
                    .instagramId(partnerMember.getInstagramId())
                    .build();

            MatchingDto resultDto = MatchingDto.builder()
                    .myInfo(myInfo)
                    .partnerInfo(partnerInfo)
                    .matchingDate(today.toString())
                    .build();

            return resultDto;
        }catch (BaseException e) {
            throw e;
        }catch (Exception e) {
            log.error("매칭 결과 조회 중 예외 발생: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
