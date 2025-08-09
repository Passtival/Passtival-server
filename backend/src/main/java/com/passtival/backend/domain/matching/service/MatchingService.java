package com.passtival.backend.domain.matching.service;

import com.passtival.backend.domain.matching.dto.MatchingResultDto;
import com.passtival.backend.domain.matching.entity.MatchingResult;
import com.passtival.backend.domain.matching.repository.MatchingResultRepository;
import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.global.common.enums.Role;
import com.passtival.backend.domain.user.repository.UserRepository;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.auth.jwt.JWTUtil;
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

    private final UserRepository userRepository;
    private final MatchingResultRepository matchingResultRepository;
    private final MatchingScheduler matchingScheduler;
    private final JWTUtil jwtUtil;

    @Transactional
    public BaseResponse<String> applyMatching(String token, User requestUser) {

        // 매칭 진행 중 체크 추가 (기존 JWT 검증 전에)
        if (matchingScheduler.isMatchingInProgress()) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "현재 매칭이 진행 중입니다. 내일 다시 시도해주세요.");
        }

        LocalTime now = LocalTime.now();
        LocalTime cutoffTime = LocalTime.of(18, 0);

        if (now.isAfter(cutoffTime)) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "오후 6시 이후에는 매칭 신청이 불가능합니다. 내일 다시 시도해주세요.");
        }
        // 1. JWT 토큰 유효성 검증
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("유효하지 않은 토큰 형식");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "토큰 형식이 올바르지 않습니다.");
        }

        String accessToken = token.substring(7); // "Bearer " 제거

        if (!jwtUtil.validateToken(accessToken)) {
            log.warn("유효하지 않은 JWT 토큰");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "유효하지 않은 토큰입니다.");
        }

        if (jwtUtil.isExpired(accessToken)) {
            log.warn("만료된 JWT 토큰");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_EXPIRED, "토큰이 만료되었습니다.");
        }

        // 2. 토큰에서 사용자 정보 추출
        Long userId = jwtUtil.getUserId(accessToken);
        String userRole = jwtUtil.getRole(accessToken);

        if (userId == null || userRole == null) {
            log.warn("토큰에서 사용자 정보 추출 실패");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        // 3. 사용자 권한 검증 (소셜 로그인 사용자만 매칭 신청 가능)
        if (!Role.ROLE_USER.name().equals(userRole)) {
            log.warn("권한 없는 사용자의 매칭 신청 시도: userId = {}, role = {}", userId, userRole);
            return BaseResponse.fail(BaseResponseStatus.ACCESS_DENIED, "매칭 신청 권한이 없습니다.");
        }

        // 4. 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            log.warn("존재하지 않는 사용자: userId = {}", userId);
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.");
        }

        // 5. 중복 신청 검증
        if (user.isApply()) {
            log.info("이미 매칭 신청한 사용자: userId = {}", userId);
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "이미 매칭 신청을 완료하였습니다.");
        }

        // 7. 매칭 신청 처리
        try {
            // 6. 인스타그램 ID 검증 (선택 입력)
            String instagramId = requestUser.getInstagramId();
            if (instagramId == null || instagramId.trim().isEmpty()) {
                user.setInstagramId("");
            } else {
                user.setInstagramId(instagramId.trim());
            }
            user.setApply(true);
            user.setApplicationTime(LocalDateTime.now());

            userRepository.save(user);

        } catch (Exception e) {
            log.error("매칭 신청 저장 중 오류 발생: userId = {}, error = {}", userId, e.getMessage());
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "매칭 신청 처리 중 오류가 발생했습니다.");
        }

        // 8. 성공 응답
        return BaseResponse.success("매칭 신청이 완료되었습니다.");
    }

    public BaseResponse<MatchingResultDto> getMatchingResult(String token) {

        // 1. JWT 토큰 유효성 검증
        if (token == null || !token.startsWith("Bearer ")) {
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "토큰 형식이 올바르지 않습니다.");
        }

        String accessToken = token.substring(7);

        if (!jwtUtil.validateToken(accessToken) || jwtUtil.isExpired(accessToken)) {
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "유효하지 않은 토큰입니다.");
        }

        // 2. 토큰에서 사용자 정보 추출
        Long userId = jwtUtil.getUserId(accessToken);
        String userRole = jwtUtil.getRole(accessToken);
        if (userId == null || userRole == null) {
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        // 3. 사용자 권한 검증 추가 (이 부분이 새로 추가되는 코드)
        if (!Role.ROLE_USER.name().equals(userRole)) {
            log.warn("권한 없는 사용자의 매칭 결과 조회 시도: userId = {}, role = {}", userId, userRole);
            return BaseResponse.fail(BaseResponseStatus.ACCESS_DENIED, "매칭 결과 조회 권한이 없습니다.");
        }

        // 3. 오늘 날짜의 매칭 결과 조회
        LocalDate today = LocalDate.now();
        Optional<MatchingResult> matchingResultOpt = matchingResultRepository
                .findUserMatchingByDate(today, userId);

        if (matchingResultOpt.isEmpty()) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "오늘 매칭 결과가 없습니다.");
        }

        MatchingResult matchingResult = matchingResultOpt.get();

        // 4. 나와 상대방의 userId 구분
        Long myUserId = userId;
        Long partnerUserId = matchingResult.getUserId1().equals(myUserId)
                ? matchingResult.getUserId2()
                : matchingResult.getUserId1();

        // 5. 사용자 정보 조회
        List<User> users = userRepository
                .findByUserIdIn(Arrays.asList(myUserId, partnerUserId));

        if (users.size() != 2) {
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "사용자 정보 조회에 실패했습니다.");
        }

        // 6. 내 정보와 상대방 정보 분리
        User myUser = users.stream()
                .filter(user -> user.getUserId().equals(myUserId))
                .findFirst()
                .orElse(null);

        User partnerUser = users.stream()
                .filter(user -> user.getUserId().equals(partnerUserId))
                .findFirst()
                .orElse(null);

        if (myUser == null || partnerUser == null) {
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }

        // 7. DTO 생성
        MatchingResultDto.UserInfo myInfo = new MatchingResultDto.UserInfo(
                myUser.getPhoneNumber(),
                myUser.getInstagramId()
        );

        MatchingResultDto.UserInfo partnerInfo = new MatchingResultDto.UserInfo(
                partnerUser.getPhoneNumber(),
                partnerUser.getInstagramId()
        );

        MatchingResultDto resultDto = new MatchingResultDto(
                myInfo,
                partnerInfo,
                today.toString()
        );

        return BaseResponse.success(resultDto);
    }
}
