package com.passtival.backend.domain.matching.service;

import com.passtival.backend.domain.matching.dto.MatchingDto;
import com.passtival.backend.domain.matching.entity.Matching;
import com.passtival.backend.domain.matching.repository.MatchingRepository;
import com.passtival.backend.domain.member.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.domain.matching.scheduler.MatchingScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public BaseResponse<String> applyMatching(Long memberId, Member requestMember) {

        // 매칭 진행 중 체크 추가 (기존 JWT 검증 전에)
        if (matchingScheduler.isMatchingInProgress()) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "현재 매칭이 진행 중입니다. 내일 다시 시도해주세요.");
        }

        LocalTime now = LocalTime.now();
        LocalTime cutoffTime = LocalTime.of(18, 0);

        if (now.isAfter(cutoffTime)) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "오후 6시 이후에는 매칭 신청이 불가능합니다. 내일 다시 시도해주세요.");
        }

        // 4. 사용자 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElse(null);

        if (member == null) {
            log.warn("존재하지 않는 사용자: memberId = {}", memberId);
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.");
        }

        // 5. 중복 신청 검증
        if (member.isApplied()) {
            log.info("이미 매칭 신청한 사용자: memberId = {}", memberId);
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "이미 매칭 신청을 완료하였습니다.");
        }

        // 7. 매칭 신청 처리
        try {
            // 6. 인스타그램 ID 검증 (선택 입력)
            String instagramId = requestMember.getInstagramId();
            if (instagramId == null || instagramId.trim().isEmpty()) {
                member.setInstagramId("");
            } else {
                member.setInstagramId(instagramId.trim());
            }
            member.setApplied(true);
            member.setAppliedAt(LocalDateTime.now());

            memberRepository.save(member);

        } catch (Exception e) {
            log.error("매칭 신청 저장 중 오류 발생: memberId = {}, error = {}", memberId, e.getMessage());
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "매칭 신청 처리 중 오류가 발생했습니다.");
        }

        // 8. 성공 응답
        return BaseResponse.success("매칭 신청이 완료되었습니다.");
    }

    public BaseResponse<MatchingDto> getMatchingResult(Long memberId) {

        // 3. 오늘 날짜의 매칭 결과 조회
        LocalDate today = LocalDate.now();
        Optional<Matching> matchingResultOpt = matchingRepository
                .findMemberMatchingByDate(today, memberId);

        if (matchingResultOpt.isEmpty()) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "오늘 매칭 결과가 없습니다.");
        }

        Matching matching = matchingResultOpt.get();

        // 4. 나와 상대방의 memberId 구분
        Long myMemberId = memberId;
        Long partnerMemberId = matching.getMaleId().equals(myMemberId)
                ? matching.getFemaleId()
                : matching.getMaleId();

        // 5. 사용자 정보 조회
        List<Member> members = memberRepository
                .findByMemberIdIn(Arrays.asList(myMemberId, partnerMemberId));

        if (members.size() != 2) {
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "사용자 정보 조회에 실패했습니다.");
        }

        // 6. 내 정보와 상대방 정보 분리
        Member myMember = members.stream()
                .filter(member -> member.getMemberId().equals(myMemberId))
                .findFirst()
                .orElse(null);

        Member partnerMember = members.stream()
                .filter(member -> member.getMemberId().equals(partnerMemberId))
                .findFirst()
                .orElse(null);

        if (myMember == null || partnerMember == null) {
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }

        // 7. DTO 생성
        MatchingDto.MemberInfo myInfo = new MatchingDto.MemberInfo(
                myMember.getPhoneNumber(),
                myMember.getInstagramId()
        );

        MatchingDto.MemberInfo partnerInfo = new MatchingDto.MemberInfo(
                partnerMember.getPhoneNumber(),
                partnerMember.getInstagramId()
        );

        MatchingDto resultDto = new MatchingDto(
                myInfo,
                partnerInfo,
                today.toString()
        );

        return BaseResponse.success(resultDto);
    }
}
