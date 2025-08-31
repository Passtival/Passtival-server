package com.passtival.backend.domain.matching.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.passtival.backend.domain.matching.model.enums.Gender;
import com.passtival.backend.global.common.enums.Role;
import com.passtival.backend.global.common.model.BaseEntity;

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
@Table(name = "matching_profiles")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MatchingProfile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(name = "social_id", length = 50)
	private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

	@Column(length = 25)
	private String name; // "홍길동"

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private Gender gender;// "MALE" 또는 "FEMALE"

	@Column(name = "phone_number", unique = true, length = 20)
	private String phoneNumber; // 제공자에 따른 형태를 0101235678로 정규화 후 저장

	@Column(name = "instagram_id", unique = true, length = 35)
	private String instagramId; // 인스타그램 ID

	@Column(name = "applied", nullable = false)
	private boolean applied;//신청 유무 기본 값 false

	@Column(name = "applied_at")
	private LocalDateTime appliedAt; // applied가 true로 바뀐 시간 (선착순 기준)

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Role role;

	//, Gender gender, String phoneNumber, String name(현재는 닉네임을 넣는중)
	public static MatchingProfile createSocialMember(String socialId, String name) {
		return MatchingProfile.builder()
			.socialId(socialId)
			.name(name)
			//.gender(gender)
			//.phoneNumber(phoneNumber)
			.instagramId(null)
			.applied(false)
			.appliedAt(null)
			.role(Role.USER)
			.build();
	}

	//== 비즈니스 로직 (상태 변경 메서드) ==//
	public void updateGender(Gender gender) {
		this.gender = gender;
	}

	public void updatePhoneNumber(String phoneNumber) {
		// 빈 문자열을 NULL로 변환
		this.phoneNumber = (phoneNumber == null || phoneNumber.trim().isEmpty()) ? null : phoneNumber;
	}

	public void updateInstagramId(String instagramId) {
		// 인스타그램 ID도 동일하게 처리
		this.instagramId = (instagramId == null || instagramId.trim().isEmpty()) ? null : instagramId;
	}

	public void applyForMatching() {
		// 매칭 신청 상태 및 시간 업데이트
		this.applied = true;
		this.appliedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
	}
}
