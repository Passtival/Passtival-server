package com.passtival.backend.domain.phoneMatch.meeting.service;

import com.passtival.backend.domain.phoneMatch.meeting.entity.PhoneMatchUser;
import com.passtival.backend.domain.phoneMatch.meeting.enums.Role;
import com.passtival.backend.domain.phoneMatch.meeting.repository.PhoneMatchUserRepository;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 매칭 관련 비즈니스 로직을 처리하는 서비스
 * 회원가입, 사용자 검증, 데이터 안전성 보장을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final PhoneMatchUserRepository phoneMatchUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    public BaseResponse<String> registerUser(PhoneMatchUser user) {

        // 1. 보안 강화: 클라이언트에서 전송할 수 있는 민감한 필드들을 서버에서 안전하게 재설정
        // - userId: null로 설정하여 JPA가 자동 생성하도록 함
        // - socialId: 현재는 빈 문자열로 통일 (향후 소셜 로그인 구현 시 변경 예정)
        // - role: 모든 신규 사용자는 일반 사용자 권한으로 제한
        // - isApply: 회원가입 시점에는 매칭 신청하지 않은 상태로 초기화
        user.setUserId(null);
        user.setSocialId("");
        user.setRole(Role.ROLE_USER);
        user.setApply(false);
        user.setApplicationTime(null);

        // 2. 비즈니스 규칙 검증: 전화번호 중복 검사
        // 전화번호는 시스템에서 사용자를 식별하는 고유 키로 사용되므로 중복 불허
        if (phoneMatchUserRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "이미 등록된 전화번호입니다.");
        }

        // 3. 보안 처리: 비밀번호 암호화
        // BCrypt 해시 함수를 사용하여 평문 비밀번호를 안전하게 암호화
        // BCrypt는 솔트를 자동으로 생성하여 레인보우 테이블 공격을 방어
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 4. 데이터 저장: 검증과 보안 처리가 완료된 사용자 정보를 데이터베이스에 저장
        try {
            phoneMatchUserRepository.save(user);
        } catch (Exception e) {
            // 데이터베이스 저장 실패 시 표준 에러 응답 반환
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR);
        }

        // 5. 성공 응답: BaseResponse 표준 형태로 성공 메시지 반환
        return BaseResponse.success("회원가입이 완료되었습니다.");
    }

    @Transactional
    public BaseResponse<String> applyMatching(String token, PhoneMatchUser requestUser) {
        
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
        PhoneMatchUser user = phoneMatchUserRepository.findById(userId)
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
            }else{
                user.setInstagramId(instagramId.trim());
            }
            user.setApply(true);
            user.setApplicationTime(LocalDateTime.now());

            phoneMatchUserRepository.save(user);

        } catch (Exception e) {
            log.error("매칭 신청 저장 중 오류 발생: userId = {}, error = {}", userId, e.getMessage());
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR, "매칭 신청 처리 중 오류가 발생했습니다.");
        }

        // 8. 성공 응답
        return BaseResponse.success("매칭 신청이 완료되었습니다.");
    }
}