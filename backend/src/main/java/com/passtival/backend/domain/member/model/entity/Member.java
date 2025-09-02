package com.passtival.backend.domain.member.model.entity;

import com.passtival.backend.global.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(name = "social_id", length = 50)
	private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

	@Column(length = 25)
	private String name; // "홍길동"

	private Boolean fistRaffle;

	private Boolean secondRaffle;

	private Boolean thirdRaffle;

	private Boolean premiumRaffle;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Role role;

	public static Member createSocialMember(String socialId, String name) {
		return Member.builder()
			.socialId(socialId)
			.name(name)
			.fistRaffle(false)
			.secondRaffle(false)
			.thirdRaffle(false)
			.premiumRaffle(false)
			.role(Role.USER)
			.build();
	}
}
