package com.passtival.backend.domain.matching.login.security.oauth2.dto;

import com.passtival.backend.domain.user.enums.Gender;
import com.passtival.backend.global.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class PhoneMatchUserDto {
    private Long userId;
    private String socialId; // "kakao_1234567890"
    private String name; // "홍길동"
    private Gender gender;// "male" 또는 "female" 카카오톡 형식을 기본으로 함
    private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장
    private Role role; // "ROLE_USER" (기본값, 추후 개발 시 추가)
}
