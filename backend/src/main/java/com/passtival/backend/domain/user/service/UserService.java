package com.passtival.backend.domain.user.service;

import com.passtival.backend.domain.user.repository.UserRepository;
import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public BaseResponse<String> registerUser(User user) {

        // 1. 보안 강화: 클라이언트에서 전송할 수 있는 민감한 필드들을 서버에서 안전하게 재설정
        // - userId: null로 설정하여 JPA가 자동 생성하도록 함
        // - socialId: 현재는 빈 문자열로 통일 (향후 소셜 로그인 구현 시 변경 예정)
        // - role: 모든 신규 사용자는 일반 사용자 권한으로 제한
        // - isApply: 회원가입 시점에는 매칭 신청하지 않은 상태로 초기화
        user.setUserId(null);
        user.setSocialId(null);
        user.setRole(Role.ROLE_USER);
        user.setApply(false);
        user.setApplicationTime(null);

        // 2. 비즈니스 규칙 검증: 전화번호 중복 검사
        // 전화번호는 시스템에서 사용자를 식별하는 고유 키로 사용되므로 중복 불허
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "이미 등록된 전화번호입니다.");
        }

        // 3. 보안 처리: 비밀번호 암호화
        // BCrypt 해시 함수를 사용하여 평문 비밀번호를 안전하게 암호화
        // BCrypt는 솔트를 자동으로 생성하여 레인보우 테이블 공격을 방어
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 4. 데이터 저장: 검증과 보안 처리가 완료된 사용자 정보를 데이터베이스에 저장
        try {
            userRepository.save(user);
        } catch (Exception e) {
            // 데이터베이스 저장 실패 시 표준 에러 응답 반환
            return BaseResponse.fail(BaseResponseStatus.DATABASE_ERROR);
        }

        // 5. 성공 응답: BaseResponse 표준 형태로 성공 메시지 반환
        return BaseResponse.success("회원가입이 완료되었습니다.");
    }
}
