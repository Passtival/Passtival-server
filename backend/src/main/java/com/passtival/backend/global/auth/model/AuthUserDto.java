package com.passtival.backend.global.auth.model;

import com.passtival.backend.domain.matching.model.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthUserDto {
	private Long userId;
	private String socialId; // "kakao_1234567890"
	private String name; // "홍길동"
	private Gender gender;// "MALE" 또는 "FEMALE"
	private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장
	private String role; // "ROLE_USER" (기본값)
}
